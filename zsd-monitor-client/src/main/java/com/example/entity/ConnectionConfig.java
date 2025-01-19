package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 连接配置类，用于存储与服务端的连接信息
 */
@Data  // 自动生成getter、setter方法
@AllArgsConstructor  // 自动生成包含所有字段的构造函数
public class ConnectionConfig {

    /**
     * 服务端的地址（如 http://192.168.0.22:8080）
     */
    String address;

    /**
     * 用于注册客户端的Token秘钥
     */
    String token;
}
