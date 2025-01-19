<script setup>
import { ref } from 'vue';
import { ElContainer, ElHeader, ElMain, ElRow, ElCol, ElCard } from 'element-plus';

// 模拟系统消息数据，添加消息类型、状态等字段
const notifications = ref([
  {
    time: "2025-01-09 15:00",
    message: "CPU 使用率过高",
    severity: "高",
    type: "告警",         // 消息类型
    status: "未读"        // 消息状态
  },
  {
    time: "2025-01-08 12:00",
    message: "内存占用异常",
    severity: "中",
    type: "操作",
    status: "已读"
  },
  {
    time: "2025-01-07 09:30",
    message: "磁盘空间不足",
    severity: "低",
    type: "系统",
    status: "未读"
  },
  {
    time: "2025-01-06 16:45",
    message: "网络连接断开",
    severity: "高",
    type: "告警",
    status: "已读"
  },
]);
</script>

<template>
  <el-container>
    <el-header class="header">
      <h2>消息通知</h2>
    </el-header>

    <el-main>
      <el-row :gutter="20">
        <!-- 消息展示 -->
        <el-col :span="24">
          <el-card class="notification-card" shadow="always">
            <h3>系统消息</h3>
            <div v-for="(notification, index) in notifications" :key="index" class="notification-item">
              <div class="time">{{ notification.time }}</div>
              <div class="type">消息类型: {{ notification.type }}</div>
              <div class="message">{{ notification.message }}</div>
              <div class="severity">严重级别: {{ notification.severity }}</div>
              <div class="status">状态: {{ notification.status }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<style scoped>
.header {
  padding: 10px;
  text-align: center;
  font-size: 20px;
}

.el-card {
  margin-bottom: 20px;
}

.notification-item {
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.notification-item .time {
  font-weight: bold;
  color: #606266;
}

.notification-item .message {
  margin-top: 5px;
  color: #409EFF;
}

.notification-item .severity {
  margin-top: 10px;
  color: #f56c6c;
}

.notification-item .status {
  margin-top: 10px;
  color: #67c23a;  /* 可以根据状态（未读/已读）使用不同颜色 */
}

.notification-item .type {
  margin-top: 5px;
  font-weight: bold;
}
</style>
