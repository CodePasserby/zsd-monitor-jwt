package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Groups;

public interface GroupsService extends IService<Groups> {
    boolean userHasAccessToGroup(Integer groupId, Integer userId);
}