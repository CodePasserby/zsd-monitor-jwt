package com.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 客户端重命名表单实体
 * 用于接收用户重命名客户端的请求
 */
@Data
public class RenameClientVO {
    // 客户端ID，不能为空
    @NotNull
    int id;

    // 客户端名称，长度要求在1到10个字符之间
    @Length(min = 1, max = 10)
    String name;
}
