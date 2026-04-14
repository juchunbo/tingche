package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.CouponTemplate;
import com.tencent.wxcloudrun.model.UserCoupon;
import com.tencent.wxcloudrun.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Coupon controller
 */
@Slf4j
@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    
    @Autowired
    private CouponService couponService;
    
    /**
     * Get active coupon templates
     */
    @GetMapping("/templates")
    public ApiResponse getCouponTemplates() {
        List<CouponTemplate> templates = couponService.getActiveCoupons();
        return ApiResponse.ok(templates);
    }
    
    /**
     * Receive coupon
     */
    @PostMapping("/receive")
    public ApiResponse receiveCoupon(@RequestHeader("X-User-Id") Long userId,
                                     @RequestBody Map<String, Long> params) {
        Long templateId = params.get("templateId");
        UserCoupon userCoupon = couponService.receiveCoupon(userId, templateId);
        return ApiResponse.ok(userCoupon);
    }
    
    /**
     * Get my coupons
     */
    @GetMapping("/my")
    public ApiResponse getMyCoupons(@RequestHeader("X-User-Id") Long userId,
                                    @RequestParam(required = false) Integer status) {
        List<UserCoupon> coupons = couponService.getMyCoupons(userId, status);
        return ApiResponse.ok(coupons);
    }
}
