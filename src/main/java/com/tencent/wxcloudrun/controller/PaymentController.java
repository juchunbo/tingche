package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器 - 云托管 unifiedOrder 模式
 * 
 * 支付回调处理：
 * - 回调通过云托管 container 内部路由完成
 * - 回调数据为明文 JSON，非 API v3 格式
 * - 以 result_code 判断支付结果，不使用 trade_state
 * - 已做幂等性处理，重复回调不会重复更新
 * - 成功必须返回 {"errcode":0,"errmsg":"ok"}，否则平台会持续重试
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * 云托管支付回调接口
     * 
     * 云托管模式下，支付成功后微信将回调发送到云托管平台，
     * 云托管平台再通过 container 内部路由转发到此接口。
     * 
     * 回调特点：
     * - 不需要验证 Wechatpay-Signature 签名（云托管已验证）
     * - 不需要解密 ciphertext（云托管已解密）
     * - 请求体为明文 JSON，字段为非 API v3 格式
     * - 以 result_code == "SUCCESS" 判断支付成功
     * - 回调可能重复发送，已做幂等性处理
     * 
     * @param body 回调数据（明文 JSON）
     * @return 严格返回 {"errcode":0,"errmsg":"ok"} 或 {"errcode":-1,"errmsg":"..."}
     */
    @PostMapping("/notify")
    public String paymentNotify(@RequestBody String body) {
        log.info("收到云托管支付回调");
        return paymentService.handlePaymentNotify(body);
    }
}
