package com.example.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 基本系统信息类，用于存储操作系统和硬件的相关信息
 */
@Data  // 自动生成getter、setter方法
@Accessors(chain = true)  // 支持链式调用
public class BaseDetail {

    /**
     * 操作系统架构（如 x86, x64）
     */
    String osArch;

    /**
     * 操作系统名称（如 Windows, Linux）
     */
    String osName;

    /**
     * 操作系统版本
     */
    String osVersion;

    /**
     * 操作系统位数（如 32 或 64）
     */
    int osBit;

    /**
     * CPU名称（如 Intel, AMD）
     */
    String cpuName;

    /**
     * CPU核心数
     */
    int cpuCore;

    /**
     * 内存大小（单位：GB）
     */
    double memory;

    /**
     * 磁盘大小（单位：GB）
     */
    double disk;

    /**
     * 当前主机IP地址
     */
    String ip;
}
