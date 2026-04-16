package com.tencent.wxcloudrun.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单创建请求DTO - 包含创建新停车订单所需的数据
 */
@Data
public class OrderCreateRequest {
    /** 要预订的停车场ID */
    private Long parkingLotId;
    
    /** 计划停车开始时间 */
    private LocalDateTime parkingStart;
    
    /** 计划停车结束时间 */
    private LocalDateTime parkingEnd;
    
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
