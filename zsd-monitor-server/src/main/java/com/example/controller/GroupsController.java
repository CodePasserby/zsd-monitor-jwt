package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Groups;
import com.example.service.GroupsService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理策略组相关操作，包括查询、添加、更新、删除等操作
 */
@Slf4j
@RestController
@RequestMapping("/api/groups")
public class GroupsController {

    @Resource
    private GroupsService groupsService;

    /**
     * 查询所有策略组
     * @return 策略组列表
     */
    @GetMapping
    public RestBean<List<Groups>> getAllGroups() {
        List<Groups> groups = groupsService.list();
        return RestBean.success(groups);
    }

    /**
     * 根据ID查询策略组
     * @param id 策略组ID
     * @return 策略组信息
     */
    @GetMapping("/{id}")
    public RestBean<Groups> getGroupById(@PathVariable Integer id) {
        Groups group = groupsService.getById(id);
        return group != null ? RestBean.success(group) : RestBean.failure(404, "策略组未找到");
    }

    /**
     * 添加策略组
     * @param group 策略组信息
     * @param userId 当前用户ID
     * @return 操作结果
     */
    @PostMapping
    public RestBean<Void> addGroup(@RequestBody @Valid Groups group,
                                   @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        System.out.println(group.getCreatedBy());
        group.setCreatedBy(userId);
        boolean success = groupsService.save(group);
        return success ? RestBean.success() : RestBean.failure(500, "添加策略组失败");
    }

    /**
     * 更新策略组
     * @param group 策略组信息
     * @return 操作结果
     */
    @PutMapping
    public RestBean<Void> updateGroup(@RequestBody @Valid Groups group) {
        boolean success = groupsService.updateById(group);
        return success ? RestBean.success() : RestBean.failure(500, "更新策略组失败");
    }

    /**
     * 删除策略组
     * @param id 策略组ID
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public RestBean<Void> deleteGroup(@PathVariable Integer id,
                                      @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                      @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.permissionCheck(userRole, id, userId)) {
            boolean success = groupsService.removeById(id);
            return success ? RestBean.success() : RestBean.failure(500, "删除策略组失败");
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 根据创建者ID查询策略组
     * @param creatorId 创建者ID
     * @return 策略组列表
     */
    @GetMapping("/creator/{creatorId}")
    public RestBean<List<Groups>> getGroupsByCreatorId(@PathVariable Integer creatorId) {
        List<Groups> groups = groupsService.lambdaQuery().eq(Groups::getCreatedBy, creatorId).list();
        return RestBean.success(groups);
    }

    /**
     * 获取策略组详细信息
     * @param id 策略组ID
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 策略组详细信息
     */
    @GetMapping("/details/{id}")
    public RestBean<Groups> getGroupDetails(@PathVariable Integer id,
                                            @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                            @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.permissionCheck(userRole, id, userId)) {
            Groups group = groupsService.getById(id);
            return group != null ? RestBean.success(group) : RestBean.failure(404, "策略组未找到");
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 辅助方法：检查是否有权限访问指定策略组
     * @param role 当前用户角色
     * @param groupId 策略组ID
     * @param userId 当前用户ID
     * @return 是否有权限
     */
    private boolean permissionCheck(String role, int groupId, int userId) {
        return Const.ROLE_ADMIN.equals(role) || groupsService.userHasAccessToGroup(groupId, userId);
    }
}