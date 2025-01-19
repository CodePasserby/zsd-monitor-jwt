package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ChangePasswordVO;
import com.example.entity.vo.request.CreateSubAccountVO;
import com.example.entity.vo.request.ModifyEmailVO;
import com.example.entity.vo.response.SubAccountVO;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理与用户相关的操作，包括修改密码、修改邮箱、创建子账户、删除子账户以及查看子账户列表
 */
@RestController  // 表示这是一个Restful API控制器
@RequestMapping("/api/user")  // 定义接口的根路径
public class UserController {

    @Resource  // 自动注入AccountService服务
    AccountService service;

    /**
     * 修改用户密码
     * @param vo 修改密码信息
     * @param userId 当前用户ID
     * @return 是否操作成功的响应
     */
    @PostMapping("/change-password")  // 定义一个POST请求的接口
    public RestBean<Void> changePassword(@RequestBody @Valid ChangePasswordVO vo,
                                         @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        // 调用AccountService进行密码修改
        return service.changePassword(userId, vo.getPassword(), vo.getNew_password()) ?
                RestBean.success() : RestBean.failure(401, "原密码输入错误！");
    }

    /**
     * 修改用户邮箱
     * @param id 当前用户ID
     * @param vo 包含新邮箱信息
     * @return 是否操作成功的响应
     */
    @PostMapping("/modify-email")  // 定义一个POST请求的接口
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid ModifyEmailVO vo) {
        String result = service.modifyEmail(id, vo);
        if(result == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(401, result);
        }
    }

    /**
     * 创建子账户
     * @param vo 包含子账户信息
     * @return 是否操作成功的响应
     */
    @PostMapping("/sub/create")  // 定义一个POST请求的接口
    public RestBean<Void> createSubAccount(@RequestBody @Valid CreateSubAccountVO vo) {
        // 调用AccountService创建子账户
        service.createSubAccount(vo);
        return RestBean.success();
    }

    /**
     * 删除子账户
     * @param uid 子账户ID
     * @param userId 当前用户ID
     * @return 是否操作成功的响应
     */
    @GetMapping("/sub/delete")  // 定义一个GET请求的接口
    public RestBean<Void> deleteSubAccount(int uid,
                                           @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        // 检查参数合法性，避免删除自己的账户
        if(uid == userId)
            return RestBean.failure(401, "非法参数");
        // 调用AccountService删除子账户
        service.deleteSubAccount(uid);
        return RestBean.success();
    }

    /**
     * 获取子账户列表
     * @return 子账户列表
     */
    @GetMapping("/sub/list")  // 定义一个GET请求的接口
    public RestBean<List<SubAccountVO>> subAccountList() {
        // 调用AccountService获取所有子账户信息
        return RestBean.success(service.listSubAccount());
    }
}
