package com.example.entity.vo.response;

import lombok.Data;

@Data
public class ClientPreviewVO {
    // 客户端ID
    int id;

    // 客户端是否在线
    boolean online;

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

    // 客户端CPU名称
    String cpuName;

    // 客户端CPU核心数
    int cpuCore;

    // 客户端内存大小（单位：GB）
    double memory;

    // 客户端CPU使用率
    double cpuUsage;

    // 客户端内存使用率
    double memoryUsage;

    // 客户端网络上传速率（单位：Mbps）
    double networkUpload;

    // 客户端网络下载速率（单位：Mbps）
    double networkDownload;
}
