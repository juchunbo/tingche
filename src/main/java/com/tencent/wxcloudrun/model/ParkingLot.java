package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Parking lot model
 */
@Data
public class ParkingLot {
    private Long id;
    private String name;
    private String city;
    private String district;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer type; // 1-Indoor 2-Outdoor
    private BigDecimal pricePerDay;
    private Integer minDays;
    private BigDecimal rating;
    private Integer monthlySales;
    private String images; // JSON array
    private String features; // JSON array
    private String description;
    private String distanceInfo;
    private String transferInfo;
    private Integer status; // 0-Disabled 1-Enabled
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
