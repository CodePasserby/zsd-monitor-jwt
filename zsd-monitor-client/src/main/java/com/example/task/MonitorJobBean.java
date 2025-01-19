package com.example.task;

import com.example.entity.RuntimeDetail;
import com.example.utils.MonitorUtils;
import com.example.utils.NetUtils;
import jakarta.annotation.Resource;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * 定时任务类，用于定期监控客户端的运行时数据
 */
@Component
public class MonitorJobBean extends QuartzJobBean {

    /**
     * 用于获取监控数据的工具类
     */
    @Resource
    MonitorUtils monitor;

    /**
     * 用于与网络相关操作的工具类
     */
    @Resource
    NetUtils net;

    /**
     * 定时任务执行的逻辑
     * 从监控工具获取客户端的运行时数据，并通过网络工具上传这些数据
     *
     * @param context Job执行上下文
     * @throws JobExecutionException 异常
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // 获取客户端的运行时数据
        RuntimeDetail runtimeDetail = monitor.monitorRuntimeDetail();

        // 将获取到的运行时数据上传至服务端
        net.updateRuntimeDetails(runtimeDetail);
    }
}
