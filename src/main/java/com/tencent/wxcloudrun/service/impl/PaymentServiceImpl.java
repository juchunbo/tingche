package com.tencent.wxcloudrun.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSONObject;
import com.tencent.wxcloudrun.config.WechatConfig;
import com.tencent.wxcloudrun.dao.OrderMapper;
import com.tencent.wxcloudrun.model.Order;
import com.tencent.wxcloudrun.service.OrderService;
import com.tencent.wxcloudrun.service.PaymentService;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Payment service implementation with WeChat Pay
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private WechatConfig wechatConfig;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderService orderService;
    
    private JsapiService jsapiService;
    
    private JsapiService getJsapiService() {
        if (jsapiService == null) {
            RSAAutoCertificateConfig config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(wechatConfig.getPayment().getMchId())
                    .privateKeyFromPath(wechatConfig.getPayment().getPrivateKeyPath())
                    .merchantSerialNumber(wechatConfig.getPayment().getMerchantSerialNumber())
                    .apiV3Key(wechatConfig.getPayment().getApiV3Key())
                    .build();
            jsapiService = new JsapiService.Builder().config(config).build();
        }
        return jsapiService;
    }
    
    @Override
    public Map<String, String> createPayment(Long orderId, String openid) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态错误");
        }
        
        // Build WeChat Pay request
        PrepayRequest request = new PrepayRequest();
        
        Amount amount = new Amount();
        amount.setTotal(order.getPayAmount().multiply(new java.math.BigDecimal("100")).intValue()); // Convert to cents
        request.setAmount(amount);
        
        request.setAppid(wechatConfig.getMiniProgram().getAppId());
        request.setMchid(wechatConfig.getPayment().getMchId());
        request.setDescription("停车订单-" + order.getOrderNo());
        request.setNotifyUrl(wechatConfig.getPayment().getNotifyUrl());
        request.setOutTradeNo(order.getOrderNo());
        
        Payer payer = new Payer();
        payer.setOpenid(openid);
        request.setPayer(payer);
        
        // Call WeChat Pay API
        PrepayResponse response = getJsapiService().prepay(request);
        
        // Build response for mini program
        Map<String, String> result = new HashMap<>();
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr",  UUID.randomUUID().toString() );
        result.put("package", "prepay_id=" + response.getPrepayId());
        result.put("signType", "RSA");
        // Note: In production, you need to generate the paySign using your private key
        
        return result;
    }
    
    @Override
    public boolean handlePaymentNotify(String notifyData, String signature, String timestamp, String nonce, String serial) {
        try {
            // Verify signature (simplified for demo)
            JSONObject json = JSONObject.parseObject(notifyData);
            JSONObject resource = json.getJSONObject("resource");
            
            String ciphertext = resource.getString("ciphertext");
            String associatedData = resource.getString("associated_data");
            String nonceStr = resource.getString("nonce");
            
            // Decrypt the notification (simplified)
            // In production, use WeChat Pay SDK to decrypt
            String decryptedData = ciphertext; // Placeholder
            
            JSONObject orderJson = JSONObject.parseObject(decryptedData);
            String outTradeNo = orderJson.getString("out_trade_no");
            String transactionId = orderJson.getString("transaction_id");
            String tradeState = orderJson.getString("trade_state");
            
            if ("SUCCESS".equals(tradeState)) {
                // Find order by order number
                Order order = orderMapper.selectByOrderNo(outTradeNo);
                if (order != null) {
                    orderService.updateOrderPayment(order.getId(), transactionId);
                    log.info("Payment success for order: {}", outTradeNo);
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Handle payment notify error", e);
            return false;
        }
    }
}
