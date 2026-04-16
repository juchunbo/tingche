package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单模型 - 代表停车预订订单
 */
@Data
public class Order {
    /** 订单唯一标识 */
    private Long id;
    
    /** 订单编号（唯一，可读性强） */
    private String orderNo;
    
    /** 创建订单的用户ID */
    private Long userId;
    
    /** 订单对应的停车场ID */
    private Long parkingLotId;
    
    /** 车主姓名 */
    private String ownerName;
    
    /** 车主联系电话 */
    private String ownerPhone;
    
    /** 车牌号码 */
    private String plateNumber;
    
    /** 计划停车开始时间 */
    private LocalDateTime parkingStart;
    
    /** 计划停车结束时间 */
    private LocalDateTime parkingEnd;
    
    /** 停车总天数 */
    private Integer parkingDays;
    
    /** 原始金额（折扣前） */
    private BigDecimal originalAmount;
    
    /** 优惠券抵扣金额 */
    private BigDecimal couponAmount;
    
    /** 实际支付金额（originalAmount - couponAmount） */
    private BigDecimal payAmount;
    
    /** 订单状态：1-待支付，2-停车中，3-已完成，4-已取消 */
    private Integer status;
    
    /** 取车码 */
    private String pickupCode;
    
    /** 接驳方式：班车、自驾、其他 */
    private String shuttleType;
    
    /** 乘坐人数（1-7人） */
    private Integer passengerCount;
    
    /** 备注/特殊要求 */
    private String remark;
    
    /** 微信支付交易号（支付成功后） */
    private String transactionId;
    
    /** 支付完成时间 */
    private LocalDateTime paidAt;
    
    /** 订单创建时间 */
    private LocalDateTime createdAt;
    
    /** 订单最后更新时间 */
    private LocalDateTime updatedAt;
    
    // === 瞬态字段（不存储到数据库，用于返回给前端） ===
    
    /** 停车场详细信息（返回订单信息时填充） */
    private transient ParkingLot parkingLot;
}
