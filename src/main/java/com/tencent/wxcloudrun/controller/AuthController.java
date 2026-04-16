package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.LoginRequest;
import com.tencent.wxcloudrun.dto.PhoneBindRequest;
import com.tencent.wxcloudrun.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器 - 云托管模式
 * 
 * 处理用户登录和手机号绑定相关接口
 * 云托管模式下，用户身份（openid/unionid）由云托管自动注入到请求 Header：
 * - x-wx-openid: 微信用户唯一标识
 * - x-wx-unionid: 跨应用统一标识（可选）
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    /** 认证服务层实例，用于处理认证业务逻辑 */
    @Autowired
    private AuthService authService;
    
    /**
     * 微信小程序登录接口（云托管模式）
     * 
     * 云托管模式下，不再需要前端传 code。
     * openid 和 unionid 由云托管自动注入到请求 Header 中：
     * - x-wx-openid: 微信用户 OpenID（必传，云托管自动注入）
     * - x-wx-unionid: 微信用户 UnionID（可选，云托管自动注入）
     * 
     * 前端只需传递用户昵称和头像即可完成登录。
     * 
     * @param openid 微信 OpenID，从请求 Header x-wx-openid 中获取（云托管自动注入）
     * @param unionid 微信 UnionID，从请求 Header x-wx-unionid 中获取（云托管自动注入，可能为空）
     * @param request 登录请求对象，包含用户昵称、头像等信息（不再包含 code）
     * @return 登录响应对象，包含用户ID、openid、昵称、头像、手机号等信息
     */
    @PostMapping("/login")
    public ApiResponse login(@RequestHeader("x-wx-openid") String openid,
                             @RequestHeader(value = "x-wx-unionid", required = false) String unionid,
                             @RequestBody LoginRequest request) {
        log.info("收到云托管登录请求: openid={}", openid);
        return ApiResponse.ok(authService.login(openid, unionid, request));
    }
    
    /**
     * 绑定手机号接口
     * 用户登录后可以绑定手机号，用于后续联系和订单通知
     * 需要从请求头获取用户ID进行身份验证
     * 
     * @param userId 用户ID，从请求头X-User-Id中获取
     * @param request 手机号绑定请求对象，包含要绑定的手机号
     * @return 操作结果，成功返回true，失败返回false
     */
    @PostMapping("/bind-phone")
    public ApiResponse bindPhone(@RequestHeader("X-User-Id") Long userId,
                                 @RequestBody PhoneBindRequest request) {
        log.info("绑定手机号请求: userId={}, phone={}", userId, request.getPhone());
        boolean success = authService.bindPhone(userId, request.getPhone());
        if (success) {
            return ApiResponse.ok();
        }
        return ApiResponse.error("绑定失败");
    }
}
