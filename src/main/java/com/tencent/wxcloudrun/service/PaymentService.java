package com.tencent.wxcloudrun.service;

import java.util.Map;

public interface PaymentService {
    Map<String, String> createPayment(Long orderId, String openid);
    boolean handlePaymentNotify(String notifyData, String signature, String timestamp, String nonce, String serial);
}
