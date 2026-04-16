package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.util.Date;


/**
 * 用户模型 - 代表系统中的用户
 */
@Data
public class User {
    /** 用户唯一标识 */
    private Long id;
    
    /** 微信OpenID - 微信用户的唯一标识 */
    private String openid;
    
    /** 微信UnionID - 跨应用统一标识 */
    private String unionid;
    
    /** 用户昵称/显示名称 */
    private String nickname;
    
    /** 用户头像图片URL */
    private String avatarUrl;
    
    /** 用户手机号（可选，可后续绑定） */
    private String phone;
    
    /** 用户创建时间 */
    private Date createdAt;
    
    /** 用户最后更新时间 */
    private Date updatedAt;
}
