package com.tencent.wxcloudrun.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.tencent.wxcloudrun.dao.OrderMapper;
import com.tencent.wxcloudrun.dao.ParkingLotMapper;
import com.tencent.wxcloudrun.dao.UserCouponMapper;
import com.tencent.wxcloudrun.dto.OrderCreateRequest;
import com.tencent.wxcloudrun.model.Order;
import com.tencent.wxcloudrun.model.ParkingLot;
import com.tencent.wxcloudrun.model.UserCoupon;
import com.tencent.wxcloudrun.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ParkingLotMapper parkingLotMapper;
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
    @Override
    @Transactional
    public Order createOrder(Long userId, OrderCreateRequest request) {
        // Get parking lot info
        ParkingLot parkingLot = parkingLotMapper.selectById(request.getParkingLotId());
        if (parkingLot == null) {
            throw new RuntimeException("停车场不存在");
        }
        
        // Calculate parking days and amount
        Duration duration = Duration.between(
                request.getParkingStart().toInstant(),
                request.getParkingEnd().toInstant()
        );

        int days = (int) duration.toDays();
        if (duration.toHours() % 24 > 0) {
            days += 1;
        }
        
        if (days < parkingLot.getMinDays()) {
            throw new RuntimeException("最少停车" + parkingLot.getMinDays() + "天");
        }
        
        BigDecimal originalAmount = parkingLot.getPricePerDay().multiply(BigDecimal.valueOf(days));
        
        // Apply coupon if provided
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (request.getCouponId() != null) {
            UserCoupon userCoupon = userCouponMapper.selectById(request.getCouponId());
            if (userCoupon != null && userCoupon.getUserId().equals(userId) && userCoupon.getStatus() == 1) {
                couponAmount = userCoupon.getCouponTemplate().getDiscountAmount();
            }
        }
        
        BigDecimal payAmount = originalAmount.subtract(couponAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }
        
        // Generate order number

        String orderNo = "P"
                + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + RandomUtil.randomNumbers(6);


        // Generate pickup code
        String pickupCode = RandomUtil.randomNumbers(6);
        
        // Create order
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setParkingLotId(request.getParkingLotId());
        order.setOwnerName(request.getOwnerName());
        order.setOwnerPhone(request.getOwnerPhone());
        order.setPlateNumber(request.getPlateNumber());
        order.setParkingStart(request.getParkingStart());
        order.setParkingEnd(request.getParkingEnd());
        order.setParkingDays(days);
        order.setOriginalAmount(originalAmount);
        order.setCouponAmount(couponAmount);
        order.setPayAmount(payAmount);
        order.setStatus(1); // Pending
        order.setPickupCode(pickupCode);
        order.setShuttleType(request.getShuttleType());
        order.setPassengerCount(request.getPassengerCount() != null ? request.getPassengerCount() : 1);
        order.setRemark(request.getRemark());
        
        orderMapper.insert(order);
        
        // Update coupon status if used
        if (request.getCouponId() != null) {
            userCouponMapper.updateStatus(request.getCouponId(), 2, order.getId());
        }
        
        return order;
    }
    
    @Override
    public Order getById(Long id) {
        return orderMapper.selectById(id);
    }
    
    @Override
    public List<Order> getByUserId(Long userId, Integer status) {
        return orderMapper.selectByUserId(userId, status);
    }
    
    @Override
    public boolean cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("只能取消待停车订单");
        }
        orderMapper.updateStatus(orderId, 4); // Cancelled
        return true;
    }
    
    @Override
    public void updateOrderPayment(Long orderId, String transactionId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && order.getStatus() == 1) {
            order.setTransactionId(transactionId);
            order.setPaidAt(new Date());
            order.setStatus(2); // Parking
            orderMapper.updateById(order);
        }
    }
}
