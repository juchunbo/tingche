package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.CouponTemplate;
import com.tencent.wxcloudrun.model.UserCoupon;

import java.util.List;

public interface CouponService {
    List<CouponTemplate> getActiveCoupons();
    UserCoupon receiveCoupon(Long userId, Long templateId);
    List<UserCoupon> getMyCoupons(Long userId, Integer status);
}
