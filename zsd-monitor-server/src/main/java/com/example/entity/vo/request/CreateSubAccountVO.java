package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 用于接收创建子账户请求的VO（值对象）
 * 包含用户名、邮箱、密码以及客户端列表
 */
@Data
public class CreateSubAccountVO {
    // 用户名，长度必须在1到10个字符之间
    @Length(min = 1, max = 10)
    String username;

    // 邮箱地址，必须符合邮箱格式
    @Email
    String email;

    // 密码，长度必须在6到20个字符之间
    @Length(min = 6, max = 20)
    String password;

    // 客户端列表，至少包含一个客户端ID
    @Size(min = 1)
    List<Integer> clients;
}
