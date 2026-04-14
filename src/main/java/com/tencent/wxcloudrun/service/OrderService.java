package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.OrderCreateRequest;
import com.tencent.wxcloudrun.model.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, OrderCreateRequest request);
    Order getById(Long id);
    List<Order> getByUserId(Long userId, Integer status);
    boolean cancelOrder(Long orderId, Long userId);
    void updateOrderPayment(Long orderId, String transactionId);
}
