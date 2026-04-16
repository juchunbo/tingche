package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.LoginRequest;
import com.tencent.wxcloudrun.dto.LoginResponse;

/**
 * 认证服务接口 - 云托管模式
 * 
 * 云托管模式下，登录方法的 openid/unionid 从请求 Header 获取，
 * 不再通过 code2Session 接口换取
 */
public interface AuthService {
    /**
     * 用户登录方法（云托管模式）
     * openid 和 unionid 由云托管自动注入到请求 Header，无需前端传 code
     * 
     * @param openid 云托管注入的 openid（从 Header x-wx-openid 获取）
     * @param unionid 云托管注入的 unionid（从 Header x-wx-unionid 获取，可能为空）
     * @param request 登录请求对象（仅包含昵称、头像，不再包含 code）
     * @return 登录响应对象，包含完整的用户信息
     */
    LoginResponse login(String openid, String unionid, LoginRequest request);
    
    /**
     * 绑定手机号方法
     * 将手机号绑定到指定用户账户，需要验证手机号格式和唯一性
     * 
     * @param userId 用户ID，要绑定手机号的账户
     * @param phone 手机号，需要绑定的电话号码
     * @return 绑定结果，成功返回true，失败返回false
     */
    boolean bindPhone(Long userId, String phone);
}
