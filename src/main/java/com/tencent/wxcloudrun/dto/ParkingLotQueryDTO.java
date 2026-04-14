package com.tencent.wxcloudrun.dto;

import lombok.Data;

/**
 * Parking lot query DTO
 */
@Data
public class ParkingLotQueryDTO {
    private String city;
    private String keyword;
    private Integer type; // 1-Indoor 2-Outdoor
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
