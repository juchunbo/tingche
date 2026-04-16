package com.tencent.wxcloudrun.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

import java.util.Date;

/**
 * 订单创建请求DTO - 包含创建新停车订单所需的数据
 */
@Data
public class OrderCreateRequest {
    /** 要预订的停车场ID */
    private Long parkingLotId;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date parkingStart;

    /** 计划停车结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date parkingEnd;

    
    /** 车主姓名 */
    private String ownerName;
    
    /** 车主联系电话 */
    private String ownerPhone;
    
    /** 车牌号码 */
    private String plateNumber;
    
    /** 接驳方式 */
    private String shuttleType;
    
    /** 乘坐人数（1-7人） */
    private Integer passengerCount;
    
    /** 备注/特殊要求 */
    private String remark;
    
    /** 使用的优惠券ID（可选，无优惠券时为null） */
    private Long couponId;
}
