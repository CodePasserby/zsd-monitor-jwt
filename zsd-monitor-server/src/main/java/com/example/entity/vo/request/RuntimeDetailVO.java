package com.example.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 运行时数据详情
 * 用于接收客户端的运行时数据，包括CPU、内存、磁盘、网络等使用情况
 */
@Data
public class RuntimeDetailVO {
    // 时间戳，表示该数据记录的时间
    @NotNull
    long timestamp;

    // CPU 使用率（百分比）
    @NotNull
    double cpuUsage;

    // 内存使用率（百分比）
    @NotNull
    double memoryUsage;

    // 磁盘使用率（百分比）
    @NotNull
    double diskUsage;

    // 网络上传速度（单位：字节）
    @NotNull
    double networkUpload;

    // 网络下载速度（单位：字节）
    @NotNull
    double networkDownload;

    // 磁盘读取速度（单位：字节）
    @NotNull
    double diskRead;

    // 磁盘写入速度（单位：字节）
    @NotNull
    double diskWrite;
}
