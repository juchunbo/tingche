package com.tencent.wxcloudrun.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.tencent.wxcloudrun.config.WechatConfig;
import com.tencent.wxcloudrun.dao.OrderMapper;
import com.tencent.wxcloudrun.model.Order;
import com.tencent.wxcloudrun.service.OrderService;
import com.tencent.wxcloudrun.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付服务实现类 - 云托管 unifiedOrder 模式
 * 
 * 下单协议严格遵循云托管开放接口服务规范：
 * - 调用地址：http://api.weixin.qq.com/_/pay/unifiedOrder
 * - 请求体字段：body, out_trade_no, total_fee, spbill_create_ip, sub_mch_id, env_id, callback_type, container
 * - 返回值从 respdata.payment 获取，直接给前端 wx.requestPayment 使用
 * 
 * 不再使用：
 * - API v3 字段：mchid, description, amount, currency
 * - APIv3 Key / 商户证书 / apiclient_key.pem
 * - RSAAutoCertificateConfig / JsapiService
 * - prepay_id 二次签名
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    
    /** 微信配置（云托管模式下仅需 appId 和 mchId） */
    @Autowired
    private WechatConfig wechatConfig;
    
    /** 云托管环境 ID（从环境变量或配置读取） */
    @Value("${wechat.cloudrun.env-id:${CLOUDRUN_ENV:}}")
    private String envId;
    
    /** 订单数据访问层 */
    @Autowired
    private OrderMapper orderMapper;
    
    /** 订单服务 */
    @Autowired
    private OrderService orderService;
    
    /** HTTP 请求工具，用于调用云托管开放接口 */
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * 创建支付订单（云托管 unifiedOrder 模式）
     * 
     * 严格遵循云托管开放接口服务 unifiedOrder 协议：
     * 1. 调用地址：http://api.weixin.qq.com/_/pay/unifiedOrder
     * 2. 请求体必须包含：body, out_trade_no, total_fee, spbill_create_ip, sub_mch_id, env_id, callback_type, container
     * 3. 不允许使用 API v3 字段：mchid, description, amount, currency
     * 4. 返回值从 respdata.payment 获取，已是可直接给前端使用的完整参数
     * 5. 后端不需要也不允许再基于 prepay_id 做二次签名
     * 
     * @param orderId 订单ID
     * @param openid 用户的微信 OpenID（从云托管 Header x-wx-openid 获取）
     * @return payment 对象，直接传给前端 wx.requestPayment
     */
    @Override
    public Map<String, String> createPayment(Long orderId, String openid) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态错误，无法支付");
        }
        
        // 第一步：构建云托管 unifiedOrder 请求体
        // 必须且只能包含以下字段（云托管协议，非 API v3）
        Map<String, Object> requestBody = new HashMap<>();
        
        // body: 商品描述
        requestBody.put("body", "停车订单-" + order.getOrderNo());
        
        // out_trade_no: 商户订单号
        requestBody.put("out_trade_no", order.getOrderNo());
        
        // total_fee: 订单金额，单位为分
        int totalFee = order.getPayAmount()
            .multiply(new BigDecimal("100"))
            .intValue();
        requestBody.put("total_fee", totalFee);
        
        // spbill_create_ip: 客户端 IP（容器内使用默认值）
        requestBody.put("spbill_create_ip", "127.0.0.1");
        
        // sub_mch_id: 子商户号（直连商户传商户号即可）
        requestBody.put("sub_mch_id", wechatConfig.getPayment().getMchId());
        
        // env_id: 云托管环境 ID（必填）
        if (envId == null || envId.isEmpty()) {
            throw new RuntimeException("云托管环境 ID 未配置，请在 application.yml 中设置 wechat.cloudrun.env-id 或环境变量 CLOUDRUN_ENV");
        }
        requestBody.put("env_id", envId);
        
        // callback_type: 回调类型，2 表示云托管 container 回调
        requestBody.put("callback_type", 2);
        
        // container: 支付回调路由（云托管 container 内部路径）
        Map<String, String> container = new HashMap<>();
        container.put("service", "springboot-ic02");  // 云托管服务名称
        container.put("path", "/api/payment/notify");  // 回调接口路径
        requestBody.put("container", container);
        
        log.info("云托管 unifiedOrder 下单请求: orderId={}, outTradeNo={}, totalFee={}, envId={}", 
            orderId, order.getOrderNo(), totalFee, envId);
        
        // 第二步：调用云托管开放接口 /_/pay/unifiedOrder
        // 注意：必须使用 http://api.weixin.qq.com/_/pay/unifiedOrder
        // 不能使用 localhost，否则不会走云托管开放接口服务
        Map<String, String> paymentParams;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 云托管开放接口地址
            String unifiedOrderUrl = "http://api.weixin.qq.com/_/pay/unifiedOrder";
            ResponseEntity<String> response = restTemplate.exchange(
                unifiedOrderUrl, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            
            log.info("云托管 unifiedOrder 响应: status={}, body={}", 
                response.getStatusCode(), response.getBody());
            
            // 第三步：解析响应，从 respdata.payment 获取 payment 对象
            JSONObject responseJson = JSONObject.parseObject(response.getBody());
            
            // 检查是否有错误
            int errcode = responseJson.getIntValue("errcode");
            if (errcode != 0) {
                String errmsg = responseJson.getString("errmsg");
                log.error("云托管 unifiedOrder 下单失败: errcode={}, errmsg={}", errcode, errmsg);
                throw new RuntimeException("支付下单失败: " + errmsg);
            }
            
            // 从 respdata.payment 获取 payment 对象
            // 该 payment 已是可直接给前端 wx.requestPayment 使用的最终参数
            // 包含：timeStamp, nonceStr, package, signType, paySign
            JSONObject respdata = responseJson.getJSONObject("respdata");
            if (respdata == null) {
                log.error("云托管 unifiedOrder 响应中无 respdata 字段: {}", response.getBody());
                throw new RuntimeException("支付下单返回数据异常");
            }
            
            JSONObject payment = respdata.getJSONObject("payment");
            if (payment == null) {
                log.error("云托管 unifiedOrder 响应中无 respdata.payment 字段: {}", response.getBody());
                throw new RuntimeException("支付下单返回数据异常，缺少 payment 字段");
            }
            
            // 直接将 payment 字段转为 Map 返回给前端
            paymentParams = new HashMap<>();
            paymentParams.put("timeStamp", payment.getString("timeStamp"));
            paymentParams.put("nonceStr", payment.getString("nonceStr"));
            paymentParams.put("package", payment.getString("package"));
            paymentParams.put("signType", payment.getString("signType"));
            paymentParams.put("paySign", payment.getString("paySign"));
            
            log.info("云托管 unifiedOrder 下单成功: orderId={}, outTradeNo={}", orderId, order.getOrderNo());
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("云托管 unifiedOrder 下单异常: orderId={}", orderId, e);
            throw new RuntimeException("支付下单失败: " + e.getMessage());
        }
        
        return paymentParams;
    }
    
    /**
     * 处理支付回调（云托管 container 内部回调）
     * 
     * 云托管 unifiedOrder 回调格式（非 API v3 格式）：
     * - 不存在 trade_state 字段
     * - 支付成功判断以 result_code == "SUCCESS" 为准
     * - 回调可能会重复发送，必须保证幂等性
     * - 成功返回 {"errcode":0,"errmsg":"ok"}，否则平台会持续重试
     * 
     * @param notifyData 回调数据（明文 JSON）
     * @return 处理结果 JSON 字符串
     */
    @Override
    public String handlePaymentNotify(String notifyData) {
        try {
            log.info("收到云托管支付回调: {}", notifyData);
            
            JSONObject json = JSONObject.parseObject(notifyData);
            
            // 云托管 unifiedOrder 回调字段（非 API v3 格式）
            String outTradeNo = json.getString("out_trade_no");      // 商户订单号
            String resultCode = json.getString("result_code");       // 业务结果
            String transactionId = json.getString("transaction_id"); // 微信支付单号
            
            log.info("云托管支付回调解析: outTradeNo={}, resultCode={}, transactionId={}", 
                outTradeNo, resultCode, transactionId);
            
            // 判断支付是否成功
            if ("SUCCESS".equals(resultCode)) {
                // 查询订单
                Order order = orderMapper.selectByOrderNo(outTradeNo);
                if (order == null) {
                    log.warn("云托管支付回调：未找到对应订单: outTradeNo={}", outTradeNo);
                    return buildErrorResponse("订单不存在");
                }
                
                // 幂等性处理：如果订单已是支付成功状态，不再重复更新
                if (order.getStatus() >= 2) {
                    log.info("云托管支付回调：订单已处理，跳过重复回调: outTradeNo={}, status={}", 
                        outTradeNo, order.getStatus());
                    return buildSuccessResponse();
                }
                
                // 更新订单支付状态
                orderService.updateOrderPayment(order.getId(), transactionId);
                log.info("云托管支付回调处理成功: outTradeNo={}, transactionId={}", 
                    outTradeNo, transactionId);
            } else {
                log.info("云托管支付回调：支付未成功: resultCode={}, outTradeNo={}", resultCode, outTradeNo);
            }
            
            return buildSuccessResponse();
        } catch (Exception e) {
            log.error("云托管支付回调处理异常", e);
            return buildErrorResponse("回调处理异常");
        }
    }
    
    /**
     * 构建成功响应
     * 云托管要求严格返回此格式，否则会持续重试回调
     * 
     * @return 成功响应 JSON
     */
    private String buildSuccessResponse() {
        return "{\"errcode\":0,\"errmsg\":\"ok\"}";
    }
    
    /**
     * 构建错误响应
     * 
     * @param message 错误信息
     * @return 错误响应 JSON
     */
    private String buildErrorResponse(String message) {
        return "{\"errcode\":-1,\"errmsg\":\"" + message + "\"}";
    }
}
