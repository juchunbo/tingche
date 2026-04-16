package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.LoginRequest;
import com.tencent.wxcloudrun.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器 - 处理用户登录和手机号绑定相关接口
 * 提供微信小程序登录、手机号注册等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    /** 认证服务层实例，用于处理认证业务逻辑 */
    @Autowired
    private AuthService authService;
    
    /**
     * 微信小程序登录接口
     * 接收小程序传来的code，调用微信API换取openid，完成用户登录
     * 
     * @param request 登录请求对象，包含微信登录code、用户昵称、头像等信息
     * @return 登录响应对象，包含用户ID、openid、昵称、头像、手机号等信息
     */
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest request) {
        log.info("收到登录请求");
        return ApiResponse.ok(authService.login(request));
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
