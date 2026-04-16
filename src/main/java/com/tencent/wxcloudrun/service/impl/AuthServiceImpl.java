package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.dto.LoginRequest;
import com.tencent.wxcloudrun.dto.LoginResponse;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类 - 云托管模式
 * 
 * 云托管模式下用户身份由云托管自动注入到请求 Header：
 * - x-wx-openid: 微信用户唯一标识
 * - x-wx-unionid: 跨应用统一标识（如果绑定了开放平台）
 * 
 * 不再需要：
 * - wx.login() 获取 code
 * - code2Session 接口换取 openid
 * - AppSecret 配置
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    /** 用户数据访问层，用于操作用户数据 */
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 用户登录实现方法（云托管模式）
     * 
     * 云托管模式下，openid 和 unionid 由云托管自动注入到请求 Header 中，
     * 前端只需调用一次云托管请求，后端从 Header 中读取即可，无需再通过 code 换取。
     * 
     * 主要流程：
     * 1. 直接使用云托管注入的 openid（从 Controller 层通过 Header 传入）
     * 2. 根据 openid 查询用户，如果不存在则创建新用户
     * 3. 如果用户已存在，则更新用户信息（昵称、头像）
     * 4. 返回用户信息给前端
     * 
     * @param openid 云托管自动注入的微信 OpenID（从请求 Header x-wx-openid 获取）
     * @param unionid 云托管自动注入的微信 UnionID（从请求 Header x-wx-unionid 获取，可能为空）
     * @param request 登录请求对象（仅包含昵称和头像，不再包含 code）
     * @return 登录响应对象
     */
    @Override
    public LoginResponse login(String openid, String unionid, LoginRequest request) {
        log.info("处理云托管登录请求: openid={}", openid);
        
        if (openid == null || openid.isEmpty()) {
            throw new RuntimeException("云托管未注入 openid，请确认已开启开放接口服务");
        }
        
        // 第一步：根据 openid 查找用户
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            // 用户不存在，创建新用户
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setNickname(request.getNickname());
            user.setAvatarUrl(request.getAvatarUrl());
            userMapper.insert(user);
            log.info("新用户创建成功: openid={}", openid);
        } else {
            // 用户已存在，更新用户信息（如果前端传了新的昵称或头像）
            if (request.getNickname() != null) {
                user.setNickname(request.getNickname());
            }
            if (request.getAvatarUrl() != null) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
            // 更新 unionid（如果之前没有，现在有了）
            if (unionid != null && !unionid.isEmpty() && 
                (user.getUnionid() == null || user.getUnionid().isEmpty())) {
                user.setUnionid(unionid);
            }
            userMapper.updateById(user);
            log.info("用户信息更新成功: openid={}", openid);
        }
        
        // 第二步：构建登录响应对象
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
