package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class RuntimeHistoryVO {
    // 磁盘使用量
    double disk;

    // 内存使用量
    double memory;

    // 存储历史数据的列表，使用LinkedList存储多个历史记录
    List<JSONObject> list = new LinkedList<>();
}
