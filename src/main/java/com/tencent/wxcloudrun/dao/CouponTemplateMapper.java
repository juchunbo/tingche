package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.CouponTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponTemplateMapper {
    
    CouponTemplate selectById(@Param("id") Long id);
    
    List<CouponTemplate> selectActiveList();
    
    int updateIssuedCount(@Param("id") Long id);
}
