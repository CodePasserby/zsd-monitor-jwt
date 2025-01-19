package com.example.entity.vo.response;

import lombok.Data;

@Data
public class ClientSimpleVO {
    // 客户端ID
    int id;

    // 客户端名称
    String name;

    // 客户端所在位置
    String location;

    // 客户端操作系统名称
    String osName;

    // 客户端操作系统版本
    String osVersion;

    // 客户端IP地址
    String ip;
}
