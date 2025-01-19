package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Map;

@Data
@TableName("strategy_groups")
public class Groups {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("group_name")
    private String groupName;

    private String description;

    @TableField("alarm_condition")
    private Map<String, Object> alarmCondition;

    @TableField("created_by")
    private Integer createdBy;

    @TableField("created_at")
    private String createdAt;
}