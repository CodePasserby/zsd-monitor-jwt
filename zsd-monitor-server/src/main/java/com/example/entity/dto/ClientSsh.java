package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

@Data
@TableName("db_client_ssh")  // 指定该类映射到数据库中的 "db_client_ssh" 表
public class ClientSsh implements BaseData {

    @TableId  // 指定id为主键
    Integer id;  // SSH连接的唯一标识符

    Integer port;  // SSH连接的端口号，默认为22

    String username;  // SSH连接的用户名

    String password;  // SSH连接的密码
}
