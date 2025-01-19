package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用于接收确认重置密码请求的VO（值对象）
 * 包含邮箱和验证码信息
 */
@Data
@AllArgsConstructor
public class ConfirmResetVO {
    // 邮箱地址，必须符合邮箱格式
    @Email
    String email;

    // 验证码，长度必须为6个字符
    @Length(max = 6, min = 6)
    String code;
}
