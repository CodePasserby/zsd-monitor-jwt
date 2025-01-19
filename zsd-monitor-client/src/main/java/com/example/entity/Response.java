package com.example.entity;

import com.alibaba.fastjson2.JSONObject;

/**
 * 响应类，表示API返回的响应结果
 */
public record Response(int id, int code, Object data, String message) {

    /**
     * 判断响应是否成功
     * @return 如果响应代码为200，则返回true，否则返回false
     */
    public boolean success() {
        return code == 200;
    }

    /**
     * 将响应数据转换为JSON格式
     * @return 返回包含数据的JSON对象
     */
    public JSONObject asJson() {
        return JSONObject.from(data);
    }

    /**
     * 将响应数据转换为字符串格式
     * @return 返回数据的字符串表示
     */
    public String asString() {
        return data.toString();
    }

    /**
     * 通过异常生成错误响应
     * @param e 异常对象
     * @return 返回一个包含错误信息的响应对象
     */
    public static Response errorResponse(Exception e) {
        return new Response(0, 500, null, e.getMessage());
    }
}
