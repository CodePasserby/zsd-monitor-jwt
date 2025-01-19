package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Client;
import com.example.entity.vo.request.ClientDetailVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.service.ClientService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 处理与客户端监控相关的操作，如客户端注册、更新客户端详细信息和运行时信息
 */
@RestController  // 表示这是一个Restful API控制器
@RequestMapping("/monitor")  // 定义接口的根路径
public class ClientController {

    @Resource  // 自动注入ClientService服务
    ClientService service;

    /**
     * 客户端注册
     * @param token 请求头中的Authorization字段，包含客户端Token
     * @return 注册是否成功的响应
     */
    @GetMapping("/register")  // 定义一个GET请求的接口
    public RestBean<Void> registerClient(@RequestHeader("Authorization") String token) {
        // 调用ClientService验证并注册客户端
        return service.verifyAndRegister(token) ?
                RestBean.success() : RestBean.failure(401, "客户端注册失败，请检查Token是否正确");
    }

    /**
     * 更新客户端详细信息
     * @param client 当前请求的客户端信息
     * @param vo 请求体中的客户端详细信息
     * @return 是否操作成功的响应
     */
    @PostMapping("/detail")  // 定义一个POST请求的接口
    public RestBean<Void> updateClientDetails(@RequestAttribute(Const.ATTR_CLIENT) Client client,
                                              @RequestBody @Valid ClientDetailVO vo) {
        // 调用ClientService更新客户端详细信息
        service.updateClientDetail(vo, client);
        return RestBean.success();
    }

    /**
     * 更新客户端运行时信息
     * @param client 当前请求的客户端信息
     * @param vo 请求体中的运行时详细信息
     * @return 是否操作成功的响应
     */
    @PostMapping("/runtime")  // 定义一个POST请求的接口
    public RestBean<Void> updateRuntimeDetails(@RequestAttribute(Const.ATTR_CLIENT) Client client,
                                               @RequestBody @Valid RuntimeDetailVO vo) {
        // 调用ClientService更新客户端运行时信息
        service.updateRuntimeDetail(vo, client);
        return RestBean.success();
    }
}
