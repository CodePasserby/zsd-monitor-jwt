package com.example.entity.vo.response;

import lombok.Data;

@Data
public class SshSettingsVO {
    // SSH连接的IP地址
    String ip;

    // SSH连接的端口，默认为22
    int port = 22;

    // SSH连接的用户名
    String username;

    // SSH连接的密码
    String password;
}
