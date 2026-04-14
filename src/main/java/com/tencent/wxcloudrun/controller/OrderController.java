package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.OrderCreateRequest;
import com.tencent.wxcloudrun.dto.OrderResponse;
import com.tencent.wxcloudrun.model.Order;
import com.tencent.wxcloudrun.model.ParkingLot;
import com.tencent.wxcloudrun.service.OrderService;
import com.tencent.wxcloudrun.service.ParkingLotService;
import com.tencent.wxcloudrun.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Order controller
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ParkingLotService parkingLotService;
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * Create order
     */
    @PostMapping
    public ApiResponse createOrder(@RequestHeader("X-User-Id") Long userId,
                                   @RequestBody OrderCreateRequest request) {
        log.info("Create order: {}", request);
        Order order = orderService.createOrder(userId, request);
        return ApiResponse.ok(order);
    }
    
    /**
     * Get order list for current user
     */
    @GetMapping
    public ApiResponse getOrderList(@RequestHeader("X-User-Id") Long userId,
                                    @RequestParam(required = false) Integer status) {
        List<Order> orders = orderService.getByUserId(userId, status);
        
        // Convert to response DTO
        List<OrderResponse> responseList = orders.stream().map(order -> {
            OrderResponse response = new OrderResponse();
            BeanUtils.copyProperties(order, response);
            
            // Get parking lot info
            ParkingLot parkingLot = parkingLotService.getById(order.getParkingLotId());
            if (parkingLot != null) {
                OrderResponse.ParkingLotInfo info = new OrderResponse.ParkingLotInfo();
                info.setId(parkingLot.getId());
                info.setName(parkingLot.getName());
                info.setAddress(parkingLot.getAddress());
                info.setImages(parkingLot.getImages());
                response.setParkingLot(info);
            }
            
            // Set status text
            response.setStatusText(getStatusText(order.getStatus()));
            
            return response;
        }).collect(Collectors.toList());
        
        return ApiResponse.ok(responseList);
    }
    
    /**
     * Get order detail
     */
    @GetMapping("/{id}")
    public ApiResponse getOrderDetail(@RequestHeader("X-User-Id") Long userId,
                                      @PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            return ApiResponse.error("订单不存在");
        }
        
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        
        ParkingLot parkingLot = parkingLotService.getById(order.getParkingLotId());
        if (parkingLot != null) {
            OrderResponse.ParkingLotInfo info = new OrderResponse.ParkingLotInfo();
            info.setId(parkingLot.getId());
            info.setName(parkingLot.getName());
            info.setAddress(parkingLot.getAddress());
            info.setImages(parkingLot.getImages());
            response.setParkingLot(info);
        }
        
        response.setStatusText(getStatusText(order.getStatus()));
        
        return ApiResponse.ok(response);
    }
    
    /**
     * Cancel order
     */
    @PostMapping("/{id}/cancel")
    public ApiResponse cancelOrder(@RequestHeader("X-User-Id") Long userId,
                                   @PathVariable Long id) {
        boolean success = orderService.cancelOrder(id, userId);
        if (success) {
            return ApiResponse.ok();
        }
        return ApiResponse.error("取消失败");
    }
    
    /**
     * Initiate payment
     */
    @PostMapping("/{id}/pay")
    public ApiResponse payOrder(@RequestHeader("X-User-Id") Long userId,
                                @RequestHeader("X-Openid") String openid,
                                @PathVariable Long id) {
        Map<String, String> payParams = paymentService.createPayment(id, openid);
        return ApiResponse.ok(payParams);
    }
    
    private String getStatusText(Integer status) {
        switch (status) {
            case 1: return "待停车";
            case 2: return "停车中";
            case 3: return "已取出";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}
