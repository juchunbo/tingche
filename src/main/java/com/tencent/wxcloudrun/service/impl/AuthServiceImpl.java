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
 * 认证服务实现类 - 实现用户登录和手机号绑定的具体业务逻辑
 * 负责与微信API交互、用户数据管理等功能
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    /** 用户数据访问层，用于操作用户数据 */
    @Autowired
    private UserMapper userMapper;
    
    /** 微信配置信息，包含AppID、AppSecret等配置 */
    @Autowired
    private WechatConfig wechatConfig;
    
    /**
     * 用户登录实现方法
     * 主要流程：
     * 1. 调用微信API，用code换取openid
     * 2. 根据openid查询用户，如果不存在则创建新用户
     * 3. 如果用户已存在，则更新用户信息（昵称、头像）
     * 4. 返回用户信息给前端
     * 
     * @param request 登录请求对象
     * @return 登录响应对象
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("处理登录请求: {}", request);
        
        // 第一步：调用微信API，用登录code换取openid和unionid
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wechatConfig.getMiniProgram().getCode2sessionUrl(),
                wechatConfig.getMiniProgram().getAppId(),
                wechatConfig.getMiniProgram().getAppSecret(),
                request.getCode());
        
        String response = HttpUtil.get(url);
        JSONObject json = JSONObject.parseObject(response);
        
        // 检查微信API返回是否有错误
        if (json.containsKey("errcode")) {
            log.error("微信登录失败: {}", response);
            throw new RuntimeException("微信登录失败");
        }
        
        String openid = json.getString("openid");
        String unionid = json.getString("unionid");
        
        // 第二步：根据openid查找用户
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            // 用户不存在，创建新用户
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setNickname(request.getNickname());
            user.setAvatarUrl(request.getAvatarUrl());
            userMapper.insert(user);
        } else {
            // 用户已存在，更新用户信息（如果前端传了新的昵称或头像）
            if (request.getNickname() != null) {
                user.setNickname(request.getNickname());
            }
            if (request.getAvatarUrl() != null) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
            userMapper.updateById(user);
        }
        
        // 第三步：构建登录响应对象
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(user.getId());
        loginResponse.setOpenid(user.getOpenid());
        loginResponse.setNickname(user.getNickname());
        loginResponse.setAvatarUrl(user.getAvatarUrl());
        loginResponse.setPhone(user.getPhone());
        
        return loginResponse;
    }
    
    /**
     * 绑定手机号实现方法
     * 主要流程：
     * 1. 验证手机号格式（中国大陆手机号）
     * 2. 检查手机号是否已被其他用户绑定
     * 3. 更新用户手机号
     * 
     * @param userId 用户ID
     * @param phone 要绑定的手机号
     * @return 绑定结果
     */
    @Override
    public boolean bindPhone(Long userId, String phone) {
        log.info("为用户绑定手机号: userId={}, phone={}", userId, phone);
        
        // 第一步：验证手机号格式（1开头，第二位3-9，总共11位）
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            log.error("手机号格式无效: {}", phone);
            return false;
        }
        
        // 第二步：检查手机号是否已被其他用户绑定
        User existingUser = userMapper.selectByPhone(phone);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            log.error("手机号 {} 已被用户 {} 绑定", phone, existingUser.getId());
            return false;
        }
        
        // 第三步：查询用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        // 第四步：更新用户手机号
        user.setPhone(phone);
        int result = userMapper.updateById(user);
        
        return result > 0;
    }
}
