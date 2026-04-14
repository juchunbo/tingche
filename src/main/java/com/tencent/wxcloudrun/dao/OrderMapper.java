package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    
    int insert(Order order);
    
    Order selectById(@Param("id") Long id);
    
    Order selectByOrderNo(@Param("orderNo") String orderNo);
    
    List<Order> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);
    
    int updateById(Order order);
    
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
