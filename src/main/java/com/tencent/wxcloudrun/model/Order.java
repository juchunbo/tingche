package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order model
 */
@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long parkingLotId;
    private String ownerName;
    private String ownerPhone;
    private String plateNumber;
    private LocalDateTime parkingStart;
    private LocalDateTime parkingEnd;
    private Integer parkingDays;
    private BigDecimal originalAmount;
    private BigDecimal couponAmount;
    private BigDecimal payAmount;
    private Integer status; // 1-Pending 2-Parking 3-Completed 4-Cancelled
    private String pickupCode;
    private String shuttleType;
    private String remark;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Transient fields for response
    private transient ParkingLot parkingLot;
}
