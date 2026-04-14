package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.ParkingLotQueryDTO;
import com.tencent.wxcloudrun.model.ParkingLot;
import com.tencent.wxcloudrun.service.ParkingLotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ApiResponse getHotList(@RequestParam(defaultValue = "10") int limit) {
        List<ParkingLot> list = parkingLotService.getHotList(limit);
        return ApiResponse.ok(list);
    }
    
    /**
     * Get parking lot detail by ID
     */
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        ParkingLot parkingLot = parkingLotService.getById(id);
        if (parkingLot == null) {
            return ApiResponse.error("停车场不存在");
        }
        return ApiResponse.ok(parkingLot);
    }
}
