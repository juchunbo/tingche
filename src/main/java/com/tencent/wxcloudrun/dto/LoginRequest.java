package com.tencent.wxcloudrun.dto;

import lombok.Data;

/**
 * 登录请求DTO - 云托管模式
 * 
 * 云托管模式下，openid 由云托管自动注入到请求 Header，
 * 不再需要前端传递 code 来换取 openid
 */
@Data
public class LoginRequest {
    /** 用户昵称（可选，从wx.getUserProfile()获取） */
    private String nickname;
    
    /** 用户头像URL（可选，从wx.getUserProfile()获取） */
    private String avatarUrl;
}
