package com.tencent.wxcloudrun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WeChat configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatConfig {
    
    private MiniProgram miniProgram = new MiniProgram();
    private Payment payment = new Payment();
    
    @Data
    public static class MiniProgram {
        private String appId;
        private String appSecret;
        private String code2sessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
    }
    
    @Data
    public static class Payment {
        private String mchId;
        private String apiV3Key;
        private String privateKeyPath;
        private String merchantSerialNumber;
        private String notifyUrl;
    }
}
