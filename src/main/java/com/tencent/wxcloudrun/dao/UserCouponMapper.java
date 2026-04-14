package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCouponMapper {
    
    int insert(UserCoupon userCoupon);
    
    List<UserCoupon> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);
    
    UserCoupon selectById(@Param("id") Long id);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("orderId") Long orderId);
}
