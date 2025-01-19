package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * 登录验证成功的用户信息响应
 * 包含了用户名、邮箱、角色、令牌和令牌过期时间等信息
 */
@Data
public class AuthorizeVO {
    // 用户名，表示用户的登录名
    String username;

    // 用户邮箱
    String email;

    // 用户角色
    String role;

    // 用户的认证令牌
    String token;

    // 令牌的过期时间
    Date expire;
}
