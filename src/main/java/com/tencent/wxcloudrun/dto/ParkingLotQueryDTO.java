package com.tencent.wxcloudrun.dto;

import lombok.Data;

/**
 * 停车场查询DTO - 包含查询停车场的过滤参数
 */
@Data
public class ParkingLotQueryDTO {
    /** 按城市名称过滤（精确匹配） */
    private String city;
    
    /** 搜索关键词，用于名称和地址的左模糊搜索 */
    private String keyword;
    
    /** 按停车场类型过滤：1-室内，2-室外 */
    private Integer type;
    
    /** 分页页码（默认：1） */
    private Integer pageNum = 1;
    
    /** 每页数量（默认：10） */
    private Integer pageSize = 10;
}
