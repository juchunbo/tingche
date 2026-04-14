package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    
    User selectByOpenid(@Param("openid") String openid);
    
    int insert(User user);
    
    int updateById(User user);
    
    User selectById(@Param("id") Long id);
}
