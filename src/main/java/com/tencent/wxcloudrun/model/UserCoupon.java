package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.util.Date;


/**
 * User coupon model
 */
@Data
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long templateId;
    private Integer status; // 1-Unused 2-Used 3-Expired
    private Date usedAt;
    private Long orderId;
    private Date expiresAt;
    private Date createdAt;
    
    // Transient fields for response
    private transient CouponTemplate couponTemplate;
}
