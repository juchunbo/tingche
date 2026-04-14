package com.tencent.wxcloudrun.dto;

import lombok.Data;

/**
 * Login request DTO
 */
@Data
public class LoginRequest {
    private String code; // WeChat login code
    private String nickname;
    private String avatarUrl;
}
