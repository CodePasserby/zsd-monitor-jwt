package com.example.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用于接收用户修改密码请求的VO（值对象）
 * 包含用户的当前密码和新密码
 */
@Data
public class ChangePasswordVO {
    // 当前密码，长度要求在6到20字符之间
    @Length(min = 6, max = 20)
    String password;

    // 新密码，长度要求在6到20字符之间
    @Length(min = 6, max = 20)
    String new_password;
}
