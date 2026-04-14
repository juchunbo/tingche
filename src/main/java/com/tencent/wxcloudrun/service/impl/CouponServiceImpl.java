package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.CouponTemplateMapper;
import com.tencent.wxcloudrun.dao.UserCouponMapper;
import com.tencent.wxcloudrun.model.CouponTemplate;
import com.tencent.wxcloudrun.model.UserCoupon;
import com.tencent.wxcloudrun.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CouponServiceImpl implements CouponService {
    
    @Autowired
    private CouponTemplateMapper couponTemplateMapper;
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
    @Override
    public List<CouponTemplate> getActiveCoupons() {
        return couponTemplateMapper.selectActiveList();
    }
    
    @Override
    public UserCoupon receiveCoupon(Long userId, Long templateId) {
        CouponTemplate template = couponTemplateMapper.selectById(templateId);
        if (template == null || template.getStatus() != 1) {
            throw new RuntimeException("优惠券不存在或已停用");
        }
        
        if (template.getIssuedCount() >= template.getTotalCount()) {
            throw new RuntimeException("优惠券已领完");
        }
        
        // Create user coupon
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setTemplateId(templateId);
        userCoupon.setStatus(1); // Unused
        userCoupon.setExpiresAt(LocalDateTime.now().plusDays(template.getValidDays()));
        
        userCouponMapper.insert(userCoupon);
        
        // Update issued count
        couponTemplateMapper.updateIssuedCount(templateId);
        
        return userCoupon;
    }
    
    @Override
    public List<UserCoupon> getMyCoupons(Long userId, Integer status) {
        return userCouponMapper.selectByUserId(userId, status);
    }
}
