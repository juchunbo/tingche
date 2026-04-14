package com.tencent.wxcloudrun.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order create request DTO
 */
@Data
public class OrderCreateRequest {
    private Long parkingLotId;
    private LocalDateTime parkingStart;
    private LocalDateTime parkingEnd;
    private String ownerName;
    private String ownerPhone;
    private String plateNumber;
    private String shuttleType;
    private String remark;
    private Long couponId; // Optional coupon
}
