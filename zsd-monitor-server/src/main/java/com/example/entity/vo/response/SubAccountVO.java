package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;

@Data
public class SubAccountVO {
    // 子账户的ID
    int id;

    // 子账户的用户名
    String username;

    // 子账户的邮箱地址
    String email;

    // 子账户关联的客户端列表
    JSONArray clientList;
}
