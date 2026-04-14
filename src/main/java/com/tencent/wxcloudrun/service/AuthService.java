package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.LoginRequest;
import com.tencent.wxcloudrun.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
