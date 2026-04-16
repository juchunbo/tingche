package com.tencent.wxcloudrun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信云托管配置类
 * 
 * 云托管模式下不再需要 AppSecret、APIv3 Key、商户证书等敏感信息
 * 用户身份（openid/unionid）由云托管自动注入到请求 Header
 * 支付由云托管开放接口服务 /_/pay/unifiedOrder 统一处理
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatConfig {
    
    /** 小程序配置（云托管模式下仅需 appId） */
    private MiniProgram miniProgram = new MiniProgram();
    
    /** 支付配置（云托管模式下仅需 mchId，无需证书和 APIv3 Key） */
    private Payment payment = new Payment();
    
    /**
     * 小程序配置
     * 云托管模式下：
     * - openid/unionid 由云托管自动注入到请求 Header（x-wx-openid / x-wx-unionid）
     * - 不再需要 AppSecret 和 code2sessionUrl
     */
    @Data
    public static class MiniProgram {
        /** 小程序 AppID（云托管控制台获取） */
        private String appId;
    }
    
    /**
     * 支付配置
     * 云托管模式下：
     * - 支付调用 /_/pay/unifiedOrder，无需 APIv3 Key 和商户证书
     * - 支付回调通过云托管 container 内部路由完成，无需 notifyUrl
     * - /_/pay/unifiedOrder 返回的 payment 字段可直接给前端 wx.requestPayment 使用
     *   后端不需要也不允许再基于 prepay_id 做二次签名
     */
    @Data
    public static class Payment {
        /** 商户号 mchId（云托管控制台获取） */
        private String mchId;
    }
}
