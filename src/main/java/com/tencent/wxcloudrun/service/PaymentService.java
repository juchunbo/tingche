package com.tencent.wxcloudrun.service;

import java.util.Map;

/**
 * 支付服务接口 - 云托管 unifiedOrder 模式
 * 
 * 云托管模式下：
 * - 统一下单通过 http://api.weixin.qq.com/_/pay/unifiedOrder
 * - 返回的 respdata.payment 字段直接给前端 wx.requestPayment 使用
 * - 支付回调通过云托管 container 内部路由完成
 * - 回调判断以 result_code 为准，不使用 trade_state
 */
public interface PaymentService {
    /**
     * 创建支付订单（云托管 unifiedOrder 模式）
     * 请求体字段：body, out_trade_no, total_fee, spbill_create_ip, sub_mch_id, env_id, callback_type, container
     * 
     * @param orderId 订单ID
     * @param openid 用户的微信 OpenID（从云托管 Header 获取）
     * @return payment 参数，直接传给前端 wx.requestPayment
     */
    Map<String, String> createPayment(Long orderId, String openid);
    
    /**
     * 处理支付回调（云托管 container 内部回调）
     * 回调格式为非 API v3：使用 result_code 判断，不使用 trade_state
     * 回调已做幂等性处理，重复回调不会重复更新订单
     * 
     * @param notifyData 回调数据（明文 JSON）
     * @return 响应 JSON 字符串：成功返回 {"errcode":0,"errmsg":"ok"}
     */
    String handlePaymentNotify(String notifyData);
}
