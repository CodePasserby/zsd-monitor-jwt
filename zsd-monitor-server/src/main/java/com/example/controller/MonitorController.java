package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.RenameClientVO;
import com.example.entity.vo.request.RenameNodeVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.request.SshConnectionVO;
import com.example.entity.vo.response.*;
import com.example.service.AccountService;
import com.example.service.ClientService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理客户端监控相关的操作，包括查看客户端列表、重命名客户端、管理节点、查看客户端详情及运行时信息等
 */
@RestController  // 标记为一个Restful API控制器
@RequestMapping("/api/monitor")  // 定义接口的根路径
public class MonitorController {

    @Resource  // 自动注入ClientService服务
    ClientService service;

    @Resource  // 自动注入AccountService服务
    AccountService accountService;

    /**
     * 获取客户端列表
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 客户端预览信息列表
     */
    @GetMapping("/list")  // 定义一个GET请求的接口
    public RestBean<List<ClientPreviewVO>> listAllClient(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                         @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        List<ClientPreviewVO> clients = service.listClients();
        // 根据用户角色判断返回的客户端列表
        if(this.isAdminAccount(userRole)) {
            return RestBean.success(clients);
        } else {
            List<Integer> ids = this.accountAccessClients(userId);
            return RestBean.success(clients.stream()
                    .filter(vo -> ids.contains(vo.getId()))
                    .toList());
        }
    }

    /**
     * 获取简化的客户端列表
     * @param userRole 当前用户角色
     * @return 简化的客户端信息列表
     */
    @GetMapping("/simple-list")  // 定义一个GET请求的接口
    public RestBean<List<ClientSimpleVO>> simpleClientList(@RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        // 只有管理员才能查看简化列表
        if(this.isAdminAccount(userRole)) {
            return RestBean.success(service.listSimpleList());
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 重命名客户端
     * @param vo 请求体中的重命名客户端信息
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 操作结果
     */
    @PostMapping("/rename")  // 定义一个POST请求的接口
    public RestBean<Void> renameClient(@RequestBody @Valid RenameClientVO vo,
                                       @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                       @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId, userRole, vo.getId())) {
            service.renameClient(vo);
            return RestBean.success();
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 重命名节点
     * @param vo 请求体中的重命名节点信息
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 操作结果
     */
    @PostMapping("/node")  // 定义一个POST请求的接口
    public RestBean<Void> renameNode(@RequestBody @Valid RenameNodeVO vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                     @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId, userRole, vo.getId())) {
            service.renameNode(vo);
            return RestBean.success();
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 获取客户端详情
     * @param clientId 客户端ID
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 客户端详细信息
     */
    @GetMapping("/details")  // 定义一个GET请求的接口
    public RestBean<ClientDetailsVO> details(int clientId,
                                             @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                             @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId, userRole, clientId)) {
            return RestBean.success(service.clientDetails(clientId));
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 获取客户端运行时信息历史
     * @param clientId 客户端ID
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 客户端运行时历史数据
     */
    @GetMapping("/runtime-history")  // 定义一个GET请求的接口
    public RestBean<RuntimeHistoryVO> runtimeDetailsHistory(int clientId,
                                                            @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                            @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId, userRole, clientId)) {
            return RestBean.success(service.clientRuntimeDetailsHistory(clientId));
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 获取客户端当前运行时信息
     * @param clientId 客户端ID
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 客户端当前运行时数据
     */
    @GetMapping("/runtime-now")  // 定义一个GET请求的接口
    public RestBean<RuntimeDetailVO> runtimeDetailsNow(int clientId,
                                                       @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                       @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId, userRole, clientId)) {
            return RestBean.success(service.clientRuntimeDetailsNow(clientId));
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 注册新的客户端Token
     * @param userRole 当前用户角色
     * @return 新注册的Token
     */
    @GetMapping("/register")  // 定义一个GET请求的接口
    public RestBean<String> registerToken(@RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole)) {
            return RestBean.success(service.registerToken());
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 删除指定客户端
     * @param clientId 客户端ID
     * @param userRole 当前用户角色
     * @return 删除结果
     */
    @GetMapping("/delete")  // 定义一个GET请求的接口
    public RestBean<String> deleteClient(int clientId,
                                         @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole)) {
            service.deleteClient(clientId);
            return RestBean.success();
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 保存客户端SSH连接信息
     * @param vo 请求体中的SSH连接信息
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 操作结果
     */
    @PostMapping("/ssh-save")  // 定义一个POST请求的接口
    public RestBean<Void> saveSshConnection(@RequestBody @Valid SshConnectionVO vo,
                                            @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                            @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId, userRole, vo.getId())) {
            service.saveClientSshConnection(vo);
            return RestBean.success();
        } else {
            return RestBean.noPermission();
        }
    }

    /**
     * 获取客户端SSH设置
     * @param clientId 客户端ID
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 客户端SSH设置
     */
    @GetMapping("/ssh")  // 定义一个GET请求的接口
    public RestBean<SshSettingsVO> sshSettings(int clientId,
                                               @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                               @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if(this.permissionCheck(userId, userRole, clientId)) {
            return RestBean.success(service.sshSettings(clientId));
        } else {
            return RestBean.noPermission();
        }
    }

    // 辅助方法：获取账户可以访问的客户端列表
    private List<Integer> accountAccessClients(int uid) {
        Account account = accountService.getById(uid);
        return account.getClientList();
    }

    // 辅助方法：检查是否为管理员账户
    private boolean isAdminAccount(String role) {
        role = role.substring(5);  // 移除角色前缀
        return Const.ROLE_ADMIN.equals(role);
    }

    // 辅助方法：检查是否有权限访问指定客户端
    private boolean permissionCheck(int uid, String role, int clientId) {
        if(this.isAdminAccount(role)) return true;
        return this.accountAccessClients(uid).contains(clientId);
    }
}
