package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 数据库中的客户端信息
 */
@Data
@TableName("db_client")  // 指定该类映射到数据库中的 "db_client" 表
@AllArgsConstructor  // 自动生成包含所有字段的构造方法
public class Client implements BaseData {

    @TableId  // 指定id为主键
    Integer id;  // 客户端的唯一标识符

    String name;  // 客户端名称

    String token;  // 客户端的认证令牌

    String location;  // 客户端的地理位置

    String node;  // 客户端所在节点

    Date registerTime;  // 客户端的注册时间
}
