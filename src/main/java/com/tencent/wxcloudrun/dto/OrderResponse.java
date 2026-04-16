package com.tencent.wxcloudrun.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 订单响应DTO - 包含返回给前端的订单信息
 */
@Data
public class OrderResponse {
    /** 订单唯一标识 */
    private Long id;
    
    /** 订单编号（可读性强） */
    private String orderNo;
    
    /** 车主姓名 */
    private String ownerName;
    
    /** 车主联系电话 */
    private String ownerPhone;
    
    /** 车牌号码 */
    private String plateNumber;
    
    /** 计划停车开始时间 */
    private Date parkingStart;
    
    /** 计划停车结束时间 */
    private Date parkingEnd;
    
    /** 停车总天数 */
    private Integer parkingDays;
    
    /** 原始金额（折扣前） */
    private BigDecimal originalAmount;
    
    /** 优惠券抵扣金额 */
    private BigDecimal couponAmount;
    
    /** 实际支付金额 */
    private BigDecimal payAmount;
    
    /** 订单状态码 */
    private Integer status;
    
    /** 取车码 */
    private String pickupCode;
    
    /** 接驳方式 */
    private String shuttleType;
    
    /** 乘坐人数 */
    private Integer passengerCount;
    
    /** 备注信息 */
    private String remark;
    
    /** 状态文本（如“待支付”、“停车中”） */
    private String statusText;
    
    /** 停车场信息（嵌套对象） */
    private ParkingLotInfo parkingLot;
    
    /**
     * 订单响应中的停车场信息嵌套类
     */
    @Data
    public static class ParkingLotInfo {
        /** 停车场ID */
        private Long id;
        
        /** 停车场名称 */
        private String name;
        
        /** 停车场地址 */
        private String address;
        
        /** 停车场第一张图片URL */
        private String images;
    }
}
