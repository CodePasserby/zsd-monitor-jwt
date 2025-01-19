package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("db_client_detail")  // 指定该类映射到数据库中的 "db_client_detail" 表
public class ClientDetail {

    @TableId  // 指定id为主键
    Integer id;  // 客户端详细信息的唯一标识符

    String osArch;  // 操作系统架构，例如 "x86_64"

    String osName;  // 操作系统名称，例如 "Windows 10"

    String osVersion;  // 操作系统版本，例如 "10.0.19042"

    int osBit;  // 操作系统位数（例如 32 或 64 位）

    String cpuName;  // CPU名称，例如 "Intel Core i7"

    int cpuCore;  // CPU核心数

    double memory;  // 内存大小，以GB为单位

    double disk;  // 硬盘大小，以GB为单位

    String ip;  // 客户端的IP地址
}
