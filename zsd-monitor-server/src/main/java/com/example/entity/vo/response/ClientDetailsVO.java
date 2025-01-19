package com.example.entity.vo.response;

import lombok.Data;

@Data
public class ClientDetailsVO {
    // 客户端ID
    int id;

    // 客户端名称
    String name;

    // 客户端是否在线
    boolean online;

    // 节点名称
    String node;

    // 客户端所在位置
    String location;

    // 客户端IP地址
    String ip;

    // CPU名称
    String cpuName;

    // 操作系统名称
    String osName;

    // 操作系统版本
    String osVersion;

    // 客户端内存大小（单位：GB）
    double memory;

    // CPU核心数
    int cpuCore;

    // 磁盘大小（单位：GB）
    double disk;
}
