package com.tencent.wxcloudrun.dto;

import lombok.Data;

/**
 * Login response DTO
 */
@Data
public class LoginResponse {
    private Long userId;
    private String openid;
    private String nickname;
    private String avatarUrl;
    private String phone;
}
