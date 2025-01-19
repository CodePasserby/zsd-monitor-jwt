package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Groups;
import com.example.mapper.GroupsMapper;
import com.example.service.GroupsService;
import org.springframework.stereotype.Service;

@Service
public class GroupsServiceImpl extends ServiceImpl<GroupsMapper, Groups> implements GroupsService {

    @Override
    public boolean userHasAccessToGroup(Integer groupId, Integer userId) {
        Groups group = this.getById(groupId);
        return group != null && group.getCreatedBy().equals(userId);
    }
}