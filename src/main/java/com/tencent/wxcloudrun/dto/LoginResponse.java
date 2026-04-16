package com.tencent.wxcloudrun.dto;

import lombok.Data;

/**
 * 登录响应DTO - 包含登录成功后的用户信息
 */
@Data
public class LoginResponse {
    /** 用户唯一标识 */
    private Long userId;
    
    /** 用户的微信OpenID */
    private String openid;
    
    /** 用户昵称 */
    private String nickname;
    
    /** 用户头像URL */
    private String avatarUrl;
    
    /** 用户手机号（如果已绑定） */
    private String phone;
}
