package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.BaseDetail;
import com.example.entity.ConnectionConfig;
import com.example.entity.Response;
import com.example.entity.RuntimeDetail;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 网络工具类，用于与服务端进行通信，发送请求和接收响应
 */
@Slf4j
@Component
public class NetUtils {
    // 创建一个HttpClient实例，用于发送HTTP请求
    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * 服务端连接配置信息
     */
    @Lazy
    @Resource
    ConnectionConfig config;

    /**
     * 向服务端注册客户端
     *
     * @param address 服务端地址
     * @param token 注册所需的Token
     * @return 是否注册成功
     */
    public boolean registerToServer(String address, String token) {
        log.info("正在向服务端注册，请稍后...");
        // 发起GET请求进行注册
        Response response = this.doGet("/register", address, token);
        if(response.success()) {
            log.info("客户端注册已完成！");
        } else {
            log.error("客户端注册失败: {}", response.message());
        }
        return response.success();
    }

    /**
     * 向服务端发起GET请求，默认使用当前配置的地址和Token
     *
     * @param url 请求的路径
     * @return 响应结果
     */
    private Response doGet(String url) {
        return this.doGet(url, config.getAddress(), config.getToken());
    }

    /**
     * 向服务端发起GET请求
     *
     * @param url 请求的路径
     * @param address 服务端地址
     * @param token 请求头中的Token
     * @return 响应结果
     */
    private Response doGet(String url, String address, String token) {
        try {
            // 构建GET请求
            HttpRequest request = HttpRequest.newBuilder().GET()
                    .uri(new URI(address + "/monitor" + url))
                    .header("Authorization", token)
                    .build();
            // 发送请求并获取响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 解析响应为Response对象
            return JSONObject.parseObject(response.body()).to(Response.class);
        } catch (Exception e) {
            log.error("在发起服务端请求时出现问题", e);
            return Response.errorResponse(e);
        }
    }

    /**
     * 向服务端更新系统基本信息
     *
     * @param detail 系统基本信息
     */
    public void updateBaseDetails(BaseDetail detail) {
        // 发起POST请求更新基本信息
        Response response = this.doPost("/detail", detail);
        if(response.success()) {
            log.info("系统基本信息已更新完成");
        } else {
            log.error("系统基本信息更新失败: {}", response.message());
        }
    }

    /**
     * 向服务端更新运行时状态
     *
     * @param detail 运行时状态信息
     */
    public void updateRuntimeDetails(RuntimeDetail detail) {
        // 发起POST请求更新运行时信息
        Response response = this.doPost("/runtime", detail);
        if(!response.success()) {
            log.warn("更新运行时状态时，接收到服务端的异常响应内容: {}", response.message());
        }
    }

    /**
     * 向服务端发起POST请求
     *
     * @param url 请求的路径
     * @param data 请求的内容
     * @return 响应结果
     */
    private Response doPost(String url, Object data) {
        try {
            // 将请求内容转换为JSON字符串
            String rawData = JSONObject.from(data).toJSONString();
            // 构建POST请求
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(rawData))
                    .uri(new URI(config.getAddress() + "/monitor" + url))
                    .header("Authorization", config.getToken())
                    .header("Content-Type", "application/json")
                    .build();
            // 发送请求并获取响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 解析响应为Response对象
            return JSONObject.parseObject(response.body()).to(Response.class);
        } catch (Exception e) {
            log.error("在发起服务端请求时出现问题", e);
            return Response.errorResponse(e);
        }
    }
}
