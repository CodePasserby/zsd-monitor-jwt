package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.dto.RuntimeData;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.RuntimeHistoryVO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class InfluxDbUtils {

    @Value("${spring.influx.url}")
    String url;  // InfluxDB 连接的 URL
    @Value("${spring.influx.user}")
    String user; // InfluxDB 用户名
    @Value("${spring.influx.password}")
    String password; // InfluxDB 密码
    String token = System.getenv("-F4QFwth-rXzYffsn9_h_4DKtaw46CodSmEfXPJfYK0m0yU2ZSTfKGQPb8kw-SSURA4F-npOn736abt4ru6_Bg=="); // Token，用于认证
    private final String BUCKET = "monitor"; // 数据存储的桶名称
    private final String ORG = "monitor";  // InfluxDB 组织名称

    private InfluxDBClient client;  // InfluxDB 客户端

    // 初始化方法，创建 InfluxDB 客户端实例
    @PostConstruct
    public void init() {
        client = InfluxDBClientFactory.create(url, user, password.toCharArray()); // 通过配置的 URL、用户名和密码创建客户端
    }

    // 将运行时数据写入 InfluxDB
    public void writeRuntimeData(int clientId, RuntimeDetailVO vo) {
        RuntimeData data = new RuntimeData();
        BeanUtils.copyProperties(vo, data);  // 将传入的 RuntimeDetailVO 属性复制到 RuntimeData 中
        data.setTimestamp(new Date(vo.getTimestamp()).toInstant());  // 设置时间戳
        data.setClientId(clientId);  // 设置客户端 ID
        WriteApiBlocking writeApi = client.getWriteApiBlocking();  // 获取阻塞式写入 API
        writeApi.writeMeasurement(BUCKET, ORG, WritePrecision.NS, data);  // 写入数据到指定的桶中，精度为纳秒
    }

    // 从 InfluxDB 读取指定客户端的历史运行数据
    public RuntimeHistoryVO readRuntimeData(int clientId) {
        RuntimeHistoryVO vo = new RuntimeHistoryVO();
        String query = """
                from(bucket: "%s")
                |> range(start: %s)
                |> filter(fn: (r) => r["_measurement"] == "runtime")
                |> filter(fn: (r) => r["clientId"] == "%s")
                """;
        String format = String.format(query, BUCKET, "-1h", clientId);  // 构造查询语句，查询过去 1 小时的数据
        List<FluxTable> tables = client.getQueryApi().query(format, ORG);  // 执行查询
        int size = tables.size();
        if (size == 0) return vo;  // 如果没有数据，返回空的结果
        List<FluxRecord> records = tables.get(0).getRecords();  // 获取查询结果中的记录
        for (int i = 0; i < records.size(); i++) {
            JSONObject object = new JSONObject();  // 创建 JSON 对象存储每条记录的数据
            object.put("timestamp", records.get(i).getTime());  // 将时间戳添加到 JSON 对象
            for (int j = 0; j < size; j++) {
                FluxRecord record = tables.get(j).getRecords().get(i);
                object.put(record.getField(), record.getValue());  // 将每个字段的值添加到 JSON 对象
            }
            vo.getList().add(object);  // 将每条记录加入结果列表
        }
        return vo;  // 返回结果
    }
}
