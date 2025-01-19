package com.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用于接收客户端详细信息的VO（值对象）
 * 包含客户端操作系统、硬件和网络等信息
 */
@Data
public class ClientDetailVO {
    // 操作系统架构，不能为空
    @NotNull
    String osArch;

    // 操作系统名称，不能为空
    @NotNull
    String osName;

    // 操作系统版本，不能为空
    @NotNull
    String osVersion;

    // 操作系统位数，不能为空
    @NotNull
    int osBit;

    // CPU名称，不能为空
    @NotNull
    String cpuName;

    // CPU核心数，不能为空
    @NotNull
    int cpuCore;

    // 内存大小，不能为空
    @NotNull
    double memory;

    // 硬盘大小，不能为空
    @NotNull
    double disk;

    // 客户端IP地址，不能为空
    @NotNull
    String ip;
}
