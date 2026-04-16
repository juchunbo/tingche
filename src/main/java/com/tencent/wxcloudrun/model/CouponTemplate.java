package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Coupon template model
 */
@Data
public class CouponTemplate {
    private Long id;
    private String name;
    private Integer type; // 1-Amount off 2-Discount
    private BigDecimal discountAmount;
    private BigDecimal minAmount;
    private BigDecimal discountRate;
    private Integer validDays;
    private Integer totalCount;
    private Integer issuedCount;
    private Integer status; // 0-Disabled 1-Enabled
    private Date createdAt;
    private Date updatedAt;
}
