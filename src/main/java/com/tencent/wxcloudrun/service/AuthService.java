package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.LoginRequest;
import com.tencent.wxcloudrun.dto.LoginResponse;

/**
 * 认证服务接口 - 定义用户认证相关的业务逻辑方法
 */
public interface AuthService {
    /**
     * 用户登录方法
     * 通过微信登录code获取openid，创建或更新用户信息
     * 
     * @param request 登录请求对象，包含微信code、昵称、头像等
     * @return 登录响应对象，包含完整的用户信息
     */
    LoginResponse login(LoginRequest request);
    
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
