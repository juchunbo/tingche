package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 停车场模型 - 代表系统中的停车场
 */
@Data
public class ParkingLot {
    /** 停车场唯一标识 */
    private Long id;
    
    /** 停车场名称 */
    private String name;
    
    /** 停车场所在城市 */
    private String city;
    
    /** 停车场所在区域/区县 */
    private String district;
    
    /** 停车场详细地址 */
    private String address;
    
    /** 地理位置经度（用于地图定位） */
    private BigDecimal longitude;
    
    /** 地理位置纬度（用于地图定位） */
    private BigDecimal latitude;
    
    /** 停车场类型：1-室内，2-室外 */
    private Integer type;
    
    /** 每天停车费用（人民币） */
    private BigDecimal pricePerDay;
    
    /** 最少预订天数 */
    private Integer minDays;
    
    /** 计费规则类型：1-自然日计费，2-24小时制计费 */
    private Integer billingType;
    
    /** 超出最低天数后每小时的费用（仅billingType=2时使用，单位：元/小时） */
    private BigDecimal pricePerHour;
    
    /** 评分（如5.0分） */
    private BigDecimal rating;
    
    /** 当月销量/预订数 */
    private Integer monthlySales;
    
    /** 停车场图片URL数组（JSON格式） */
    private String images;
    
    /** 特色标签数组（JSON格式，如["免费接送", "24小时监控"]） */
    private String features;
    
    /** 停车场描述和计费规则 */
    private String description;
    
    /** 距离信息（如"直线距离1.4公里"） */
    private String distanceInfo;
    
    /** 接驳信息（如"平均接驳4分钟车程"） */
    private String transferInfo;
    
    /** 状态：0-停用，1-启用（可预订） */
    private Integer status;
    
    /** 停车场创建时间 */
    private LocalDateTime createdAt;
    
    /** 停车场最后更新时间 */
    private LocalDateTime updatedAt;
}
