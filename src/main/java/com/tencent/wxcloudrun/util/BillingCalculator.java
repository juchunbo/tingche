package com.tencent.wxcloudrun.util;

import com.tencent.wxcloudrun.model.ParkingLot;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 停车费用计算工具类
 * 支持两种计费规则：
 * 1. 自然日计费 - 按自然天数计算，跨天即算一天
 * 2. 24小时制计费 - 按实际小时计算，满24小时算一天，不足按小时收费
 */
public class BillingCalculator {
    
    /**
     * 计算停车费用
     * 
     * @param parkingLot 停车场信息
     * @param startTime 停车开始时间
     * @param endTime 停车结束时间
     * @return 费用计算结果对象
     */
    public static BillingResult calculate(ParkingLot parkingLot, LocalDateTime startTime, LocalDateTime endTime) {
        BillingResult result = new BillingResult();
        
        // 计算停车时长
        Duration duration = Duration.between(startTime, endTime);
        long totalHours = duration.toHours();
        long totalMinutes = duration.toMinutes() % 60;
        
        result.setTotalHours(totalHours);
        result.setTotalMinutes(totalMinutes);
        
        // 根据计费类型计算费用
        if (parkingLot.getBillingType() == 2) {
            // 24小时制计费
            result = calculateBy24HourSystem(parkingLot, totalHours, totalMinutes, result);
        } else {
            // 自然日计费（默认）
            result = calculateByNaturalDay(parkingLot, startTime, endTime, result);
        }
        
        return result;
    }
    
    /**
     * 自然日计费规则
     * 只要跨越了不同的自然日，就按多天计算
     * 例如：5号23点到6号1点 = 2天
     * 
     * @param parkingLot 停车场信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param result 结果对象
     * @return 计算后的结果
     */
    private static BillingResult calculateByNaturalDay(ParkingLot parkingLot, 
                                                       LocalDateTime startTime, 
                                                       LocalDateTime endTime,
                                                       BillingResult result) {
        result.setBillingType(1);
        result.setBillingTypeName("自然日计费");
        
        // 计算跨越的自然日天数
        // 例如：5号到6号 = 2天，即使只停了2小时
        int startDay = startTime.getDayOfMonth();
        int endDay = endTime.getDayOfMonth();
        
        // 如果跨月，需要特殊处理
        long naturalDays;
        if (startTime.getMonth().equals(endTime.getMonth()) && 
            startTime.getYear() == endTime.getYear()) {
            // 同月：直接计算日期差
            naturalDays = endDay - startDay + 1;
        } else {
            // 跨月或跨年：使用toLocalDate计算
            naturalDays = Duration.between(
                startTime.toLocalDate().atStartOfDay(),
                endTime.toLocalDate().atStartOfDay()
            ).toDays() + 1;
        }
        
        // 确保最少1天
        naturalDays = Math.max(1, naturalDays);
        result.setDays((int) naturalDays);
        result.setHours(0);
        
        // 计算费用：天数 × 每天单价
        BigDecimal totalAmount = parkingLot.getPricePerDay()
            .multiply(BigDecimal.valueOf(naturalDays));
        
        result.setDaysAmount(totalAmount);
        result.setExtraHoursAmount(BigDecimal.ZERO);
        result.setTotalAmount(totalAmount);
        
        // 生成计费说明
        result.setDescription(String.format("自然日计费：%d天 × %.2f元/天 = %.2f元",
            naturalDays, parkingLot.getPricePerDay(), totalAmount));
        
        return result;
    }
    
    /**
     * 24小时制计费规则
     * 满24小时算1天，不足24小时的部分：
     * - 如果在最低天数内，按天计算
     * - 如果超出最低天数，按小时计算
     * 
     * @param parkingLot 停车场信息
     * @param totalHours 总小时数
     * @param totalMinutes 剩余分钟数
     * @param result 结果对象
     * @return 计算后的结果
     */
    private static BillingResult calculateBy24HourSystem(ParkingLot parkingLot, 
                                                         long totalHours,
                                                         long totalMinutes,
                                                         BillingResult result) {
        result.setBillingType(2);
        result.setBillingTypeName("24小时制计费");
        
        int minDays = parkingLot.getMinDays();
        long minHours = minDays * 24;
        
        int actualDays;
        int extraHours = 0;
        BigDecimal daysAmount;
        BigDecimal extraHoursAmount;
        BigDecimal totalAmount;
        
        if (totalHours < minHours) {
            // 未达到最低天数，按最低天数计算
            actualDays = minDays;
            extraHours = 0;
            
            // 费用 = 最低天数 × 每天单价
            daysAmount = parkingLot.getPricePerDay()
                .multiply(BigDecimal.valueOf(minDays));
            extraHoursAmount = BigDecimal.ZERO;
            totalAmount = daysAmount;
            
            result.setDescription(String.format("24小时制计费：不足最低%d天，按%d天计算 × %.2f元/天 = %.2f元",
                minDays, minDays, parkingLot.getPricePerDay(), totalAmount));
        } else {
            // 达到或超过最低天数
            long fullDays = totalHours / 24;
            extraHours = (int) (totalHours % 24);
            
            // 如果有剩余分钟，且超出最低天数，额外小时+1
            if (totalMinutes > 0 && fullDays >= minDays) {
                extraHours += 1;
            }
            
            actualDays = (int) fullDays;
            
            // 计算基础天数费用
            daysAmount = parkingLot.getPricePerDay()
                .multiply(BigDecimal.valueOf(actualDays));
            
            // 计算超出部分的小时费用
            if (extraHours > 0) {
                BigDecimal pricePerHour = parkingLot.getPricePerHour();
                if (pricePerHour == null) {
                    // 如果没有设置小时价格，使用天价格/24
                    pricePerHour = parkingLot.getPricePerDay()
                        .divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP);
                }
                
                extraHoursAmount = pricePerHour
                    .multiply(BigDecimal.valueOf(extraHours));
            } else {
                extraHoursAmount = BigDecimal.ZERO;
            }
            
            totalAmount = daysAmount.add(extraHoursAmount);
            
            result.setDescription(String.format("24小时制计费：%d天 × %.2f元/天 + %d小时 × %.2f元/小时 = %.2f元",
                actualDays, parkingLot.getPricePerDay(), 
                extraHours, parkingLot.getPricePerHour() != null ? parkingLot.getPricePerHour() : 0,
                totalAmount));
        }
        
        result.setDays(actualDays);
        result.setHours(extraHours);
        result.setDaysAmount(daysAmount);
        result.setExtraHoursAmount(extraHoursAmount);
        result.setTotalAmount(totalAmount);
        
        return result;
    }
    
    /**
     * 获取计费规则说明文本（用于前端展示）
     * 
     * @param parkingLot 停车场信息
     * @return 计费规则说明
     */
    public static String getBillingRuleDescription(ParkingLot parkingLot) {
        StringBuilder desc = new StringBuilder();
        
        if (parkingLot.getBillingType() == 2) {
            // 24小时制计费说明
            desc.append("【24小时制计费规则】\n");
            desc.append("• 按实际停车时长计算，满24小时为1天\n");
            desc.append("• 最低预订").append(parkingLot.getMinDays()).append("天\n");
            desc.append("• 超出最低天数后，按小时计费\n");
            desc.append("• 每小时费用：").append(parkingLot.getPricePerHour() != null ? 
                parkingLot.getPricePerHour() + "元" : "（天价格÷24）");
        } else {
            // 自然日计费说明
            desc.append("【自然日计费规则】\n");
            desc.append("• 按自然日计算，跨越不同日期即算多天\n");
            desc.append("• 例如：5号23:00到6号01:00，按2天计算\n");
            desc.append("• 最低预订").append(parkingLot.getMinDays()).append("天\n");
            desc.append("• 每天费用：").append(parkingLot.getPricePerDay()).append("元");
        }
        
        return desc.toString();
    }
    
    /**
     * 费用计算结果
     */
    public static class BillingResult {
        /** 计费类型：1-自然日，2-24小时制 */
        private Integer billingType;
        
        /** 计费类型名称 */
        private String billingTypeName;
        
        /** 停车天数 */
        private Integer days;
        
        /** 超出天数的小时数 */
        private Integer hours;
        
        /** 总小时数 */
        private Long totalHours;
        
        /** 剩余分钟数 */
        private Long totalMinutes;
        
        /** 天数费用 */
        private BigDecimal daysAmount;
        
        /** 超出小时费用 */
        private BigDecimal extraHoursAmount;
        
        /** 总费用 */
        private BigDecimal totalAmount;
        
        /** 计费说明 */
        private String description;
        
        // Getters and Setters
        public Integer getBillingType() { return billingType; }
        public void setBillingType(Integer billingType) { this.billingType = billingType; }
        
        public String getBillingTypeName() { return billingTypeName; }
        public void setBillingTypeName(String billingTypeName) { this.billingTypeName = billingTypeName; }
        
        public Integer getDays() { return days; }
        public void setDays(Integer days) { this.days = days; }
        
        public Integer getHours() { return hours; }
        public void setHours(Integer hours) { this.hours = hours; }
        
        public Long getTotalHours() { return totalHours; }
        public void setTotalHours(Long totalHours) { this.totalHours = totalHours; }
        
        public Long getTotalMinutes() { return totalMinutes; }
        public void setTotalMinutes(Long totalMinutes) { this.totalMinutes = totalMinutes; }
        
        public BigDecimal getDaysAmount() { return daysAmount; }
        public void setDaysAmount(BigDecimal daysAmount) { this.daysAmount = daysAmount; }
        
        public BigDecimal getExtraHoursAmount() { return extraHoursAmount; }
        public void setExtraHoursAmount(BigDecimal extraHoursAmount) { this.extraHoursAmount = extraHoursAmount; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
