package com.tencent.wxcloudrun.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order response DTO
 */
@Data
public class OrderResponse {
    private Long id;
    private String orderNo;
    private String ownerName;
    private String ownerPhone;
    private String plateNumber;
    private LocalDateTime parkingStart;
    private LocalDateTime parkingEnd;
    private Integer parkingDays;
    private BigDecimal originalAmount;
    private BigDecimal couponAmount;
    private BigDecimal payAmount;
    private Integer status;
    private String pickupCode;
    private String shuttleType;
    private Integer passengerCount;
    private String remark;
    private String statusText;
    private ParkingLotInfo parkingLot;
    
    @Data
    public static class ParkingLotInfo {
        private Long id;
        private String name;
        private String address;
        private String images;
    }
}
