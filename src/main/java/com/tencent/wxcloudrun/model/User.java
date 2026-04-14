package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * User model
 */
@Data
public class User {
    private Long id;
    private String openid;
    private String unionid;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
