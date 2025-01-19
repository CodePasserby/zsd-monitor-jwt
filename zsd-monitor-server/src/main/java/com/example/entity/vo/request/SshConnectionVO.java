package com.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * SSH 连接信息
 * 用于接收用户输入的SSH连接配置信息，包括连接ID、端口、用户名和密码等
 */
@Data
public class SshConnectionVO {
    // 连接ID，用于唯一标识某个SSH连接
    int id;

    // 端口号，表示SSH连接的端口
    int port;

    // 用户名，必须提供并且长度至少为1
    @NotNull
    @Length(min = 1)
    String username;

    // 密码，必须提供并且长度至少为1
    @NotNull
    @Length(min = 1)
    String password;
}
