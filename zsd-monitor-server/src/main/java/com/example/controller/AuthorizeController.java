package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

/**
 * 用于验证相关的Controller，包含用户的注册、重置密码等操作
 */
@Validated  // 开启Spring的验证功能
@RestController  // 表示这是一个Restful API控制器
@RequestMapping("/api/auth")  // 定义接口的根路径
@Tag(name = "登录校验相关", description = "包括用户登录、注册、验证码请求等操作。")  // 用于Swagger的接口文档说明
public class AuthorizeController {

    @Resource  // 自动注入AccountService服务
    AccountService accountService;

    /**
     * 请求邮件验证码
     * @param email 请求邮件
     * @param type 类型，reset为重置密码，modify为修改邮箱
     * @param request HttpServletRequest请求对象
     * @return 是否请求成功的响应
     */
    @GetMapping("/ask-code")  // 定义一个GET请求的接口
    @Operation(summary = "请求邮件验证码")  // 用于Swagger的接口文档说明
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,  // 验证邮箱格式
                                        @RequestParam @Pattern(regexp = "(reset|modify)") String type,  // 验证类型格式
                                        HttpServletRequest request){  // 获取请求的远程IP地址
        return this.messageHandle(() ->  // 调用处理方法
                accountService.registerEmailVerifyCode(type, String.valueOf(email), request.getRemoteAddr()));
    }

    /**
     * 执行密码重置确认，检查验证码是否正确
     * @param vo 密码重置信息
     * @return 是否操作成功的响应
     */
    @PostMapping("/reset-confirm")  // 定义一个POST请求的接口
    @Operation(summary = "密码重置确认")  // 用于Swagger的接口文档说明
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){  // 验证请求体是否有效
        return this.messageHandle(() -> accountService.resetConfirm(vo));  // 调用确认重置密码的方法
    }

    /**
     * 执行密码重置操作
     * @param vo 密码重置信息
     * @return 是否操作成功的响应
     */
    @PostMapping("/reset-password")  // 定义一个POST请求的接口
    @Operation(summary = "密码重置操作")  // 用于Swagger的接口文档说明
    public RestBean<Void> resetPassword(@RequestBody @Valid EmailResetVO vo){  // 验证请求体是否有效
        return this.messageHandle(() ->  // 调用重置密码的方法
                accountService.resetEmailAccountPassword(vo));
    }

    /**
     * 针对于返回值为String作为错误信息的方法进行统一处理
     * @param action 具体操作
     * @return 响应结果，成功时返回空，失败时返回错误信息
     * @param <T> 响应结果类型
     */
    private <T> RestBean<T> messageHandle(Supplier<String> action){  // 处理操作并返回结果
        String message = action.get();  // 获取操作结果
        if(message == null)  // 如果没有错误信息，表示成功
            return RestBean.success();
        else  // 否则返回失败，携带错误信息
            return RestBean.failure(400, message);
    }
}
