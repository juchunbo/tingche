package com.tencent.wxcloudrun.dto;

import lombok.Data;

/**
 * 登录请求DTO - 包含微信登录所需的数据
 */
@Data
public class LoginRequest {
    /** 微信登录code（通过wx.login()获取） */
    private String code;
    
    /** 用户昵称（可选，从wx.getUserProfile()获取） */
    private String nickname;
    
    /** 用户头像URL（可选，从wx.getUserProfile()获取） */
    private String avatarUrl;
}
