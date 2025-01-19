package com.example.entity.dto;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

@Data
@Measurement(name = "runtime")  // 标识这是一个 "runtime" 的测量数据，用于InlfuxDB数据库
public class RuntimeData {

    @Column(tag = true)  // 标识该字段为标签，用于数据查询时索引
    int clientId;  // 客户端ID，用于标识数据所属的客户端

    @Column(timestamp = true)  // 标识该字段为时间戳字段
    Instant timestamp;  // 数据的时间戳，表示数据记录的时间

    @Column  // 普通列，存储CPU使用率
    double cpuUsage;  // CPU的使用率，表示百分比

    @Column  // 普通列，存储内存使用率
    double memoryUsage;  // 内存的使用率，表示百分比

    @Column  // 普通列，存储磁盘使用率
    double diskUsage;  // 磁盘的使用率，表示百分比

    @Column  // 普通列，存储网络上传数据量
    double networkUpload;  // 网络上传的数据量，单位为字节

    @Column  // 普通列，存储网络下载数据量
    double networkDownload;  // 网络下载的数据量，单位为字节

    @Column  // 普通列，存储磁盘读取数据量
    double diskRead;  // 磁盘读取的数据量，单位为字节

    @Column  // 普通列，存储磁盘写入数据量
    double diskWrite;  // 磁盘写入的数据量，单位为字节
}
