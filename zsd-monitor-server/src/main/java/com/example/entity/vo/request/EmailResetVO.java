package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 密码重置表单实体
 * 用于接收用户通过邮箱重置密码的请求
 */
@Data
public class EmailResetVO {
    // 邮箱地址，必须符合邮箱格式
    @Email
    String email;

    // 验证码，长度必须为6个字符
    @Length(max = 6, min = 6)
    String code;

    // 新密码，长度必须在6到20个字符之间
    @Length(min = 6, max = 20)
    String password;
}
