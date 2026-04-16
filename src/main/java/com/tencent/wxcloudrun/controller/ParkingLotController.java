package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.ParkingLotQueryDTO;
import com.tencent.wxcloudrun.model.ParkingLot;
import com.tencent.wxcloudrun.service.ParkingLotService;
import com.tencent.wxcloudrun.util.BillingCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parking lot controller
 */
@Slf4j
@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {
    
    @Autowired
    private ParkingLotService parkingLotService;
    
    /**
     * Get parking lot list with search and filter
     */
    @GetMapping
    public ApiResponse getList(ParkingLotQueryDTO query) {
        log.info("Get parking lots: {}", query);
        List<ParkingLot> list = parkingLotService.getList(query);
        return ApiResponse.ok(list);
    }
    
    /**
     * Get hot parking lots
     */
    @GetMapping("/hot")
    public ApiResponse getHotList(@RequestParam(required = false) String city,
                                  @RequestParam(defaultValue = "10") int limit) {
        List<ParkingLot> list = parkingLotService.getHotList(city, limit);
        return ApiResponse.ok(list);
    }
    
    /**
     * 获取停车场详情
     * 返回停车场信息以及计费规则说明
     * 
     * @param id 停车场ID
     * @return 停车场详情和计费规则
     */
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        ParkingLot parkingLot = parkingLotService.getById(id);
        if (parkingLot == null) {
            return ApiResponse.error("停车场不存在");
        }
        
        // 构建响应数据，包含停车场信息和计费规则说明
        Map<String, Object> result = new HashMap<>();
        result.put("parkingLot", parkingLot);
        result.put("billingRule", BillingCalculator.getBillingRuleDescription(parkingLot));
        
        return ApiResponse.ok(result);
    }
}
