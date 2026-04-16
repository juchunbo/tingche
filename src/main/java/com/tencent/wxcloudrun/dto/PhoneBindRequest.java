package com.tencent.wxcloudrun.dto;

import lombok.Data;

/**
 * 手机号绑定请求DTO - 包含将手机号绑定到用户账户的数据
 */
@Data
public class PhoneBindRequest {
    /** 要绑定的手机号（格式：1[3-9]后跟9位数字） */
    private String phone;
    
    /** 短信验证码（可选，用于未来的验证功能） */
    private String code;
}
