package com.example.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 运行时数据类，用于表示客户端的运行时详细数据
 */
@Data
@Accessors(chain = true)
public class RuntimeDetail {
    /**
     * 时间戳，记录数据的时间
     */
    long timestamp;

    /**
     * CPU使用率
     */
    double cpuUsage;

    /**
     * 内存使用率
     */
    double memoryUsage;

    /**
     * 磁盘使用率
     */
    double diskUsage;

    /**
     * 网络上传流量
     */
    double networkUpload;

    /**
     * 网络下载流量
     */
    double networkDownload;

    /**
     * 磁盘读取流量
     */
    double diskRead;

    /**
     * 磁盘写入流量
     */
    double diskWrite;
}
