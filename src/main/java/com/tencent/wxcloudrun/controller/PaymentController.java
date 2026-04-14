package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Payment controller
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * WeChat payment callback
     */
    @PostMapping("/notify")
    public String paymentNotify(@RequestBody String body,
                                @RequestHeader("Wechatpay-Signature") String signature,
                                @RequestHeader("Wechatpay-Timestamp") String timestamp,
                                @RequestHeader("Wechatpay-Nonce") String nonce,
                                @RequestHeader("Wechatpay-Serial") String serial) {
        log.info("Payment notify received");
        
        boolean success = paymentService.handlePaymentNotify(body, signature, timestamp, nonce, serial);
        
        if (success) {
            return "{\"code\":\"SUCCESS\",\"message\":\"成功\"}";
        } else {
            return "{\"code\":\"FAIL\",\"message\":\"失败\"}";
        }
    }
}
