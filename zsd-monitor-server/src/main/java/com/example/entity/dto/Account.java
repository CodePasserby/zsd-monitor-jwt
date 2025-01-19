package com.example.entity.dto;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 数据库中的用户信息
 */
@Data
@TableName("db_account")  // 指定该类映射到数据库中的 "db_account" 表
@AllArgsConstructor  // 自动生成包含所有字段的构造方法
public class Account implements BaseData {

    @TableId(type = IdType.AUTO)  // 设置id为自增类型
    Integer id;  // 用户的唯一标识符

    String username;  // 用户名

    String password;  // 用户密码

    String email;  // 用户邮箱地址

    String role;  // 用户角色

    Date registerTime;  // 注册时间

    String clients;  // 与用户关联的客户端列表，以字符串形式存储

    /**
     * 获取用户关联的客户端ID列表
     * @return 客户端ID列表
     */
    public List<Integer> getClientList() {
        // 如果客户端列表为空，返回一个空列表
        if (clients == null) return Collections.emptyList();
        // 将字符串形式的客户端列表解析为整数列表
        return JSONArray.parse(clients).toList(Integer.class);
    }
}
