package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * User coupon model
 */
@Data
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long templateId;
    private Integer status; // 1-Unused 2-Used 3-Expired
    private LocalDateTime usedAt;
    private Long orderId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    
    // Transient fields for response
    private transient CouponTemplate couponTemplate;
}
