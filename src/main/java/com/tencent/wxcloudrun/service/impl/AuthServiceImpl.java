package com.tencent.wxcloudrun.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.dto.LoginRequest;
import com.tencent.wxcloudrun.dto.LoginResponse;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.AuthService;
import com.tencent.wxcloudrun.config.WechatConfig;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Auth service implementation
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private WechatConfig wechatConfig;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login request: {}", request);
        
        // Call WeChat API to get openid
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wechatConfig.getMiniProgram().getCode2sessionUrl(),
                wechatConfig.getMiniProgram().getAppId(),
                wechatConfig.getMiniProgram().getAppSecret(),
                request.getCode());
        
        String response = HttpUtil.get(url);
        JSONObject json = JSONObject.parseObject(response);
        
        if (json.containsKey("errcode")) {
            log.error("WeChat login failed: {}", response);
            throw new RuntimeException("微信登录失败");
        }
        
        String openid = json.getString("openid");
        String unionid = json.getString("unionid");
        
        // Find or create user
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setNickname(request.getNickname());
            user.setAvatarUrl(request.getAvatarUrl());
            userMapper.insert(user);
        } else {
            // Update user info if provided
            if (request.getNickname() != null) {
                user.setNickname(request.getNickname());
            }
            if (request.getAvatarUrl() != null) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
            userMapper.updateById(user);
        }
        
        // Build response
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(user.getId());
        loginResponse.setOpenid(user.getOpenid());
        loginResponse.setNickname(user.getNickname());
        loginResponse.setAvatarUrl(user.getAvatarUrl());
        loginResponse.setPhone(user.getPhone());
        
        return loginResponse;
    }
}
