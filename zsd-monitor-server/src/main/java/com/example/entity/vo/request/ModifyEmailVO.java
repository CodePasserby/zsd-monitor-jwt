package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 修改邮箱表单实体
 * 用于接收用户修改邮箱的请求
 */
@Data
public class ModifyEmailVO {
    // 新的邮箱地址，必须符合邮箱格式
    @Email
    String email;

    // 验证码，长度必须为6个字符
    @Length(max = 6, min = 6)
    String code;
}
