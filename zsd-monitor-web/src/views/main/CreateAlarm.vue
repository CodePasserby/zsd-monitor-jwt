<template>
  <el-container class="alarm-container">
    <!-- 页面头部 -->
    <el-header class="alarm-header">
      <h2>告警策略管理</h2>
      <el-button type="primary" size="small" @click="openCreateDialog">新增告警</el-button>
    </el-header>

    <!-- 告警策略列表 -->
    <el-main>
      <el-table :data="alarmStrategies" style="width: 100%">
        <el-table-column prop="name" label="告警名称" width="200" />
        <el-table-column prop="strategyGroupName" label="策略组" width="200" />
        <el-table-column prop="target" label="目标对象" />
        <el-table-column prop="notifyMethods" label="接收方式">
          <template #default="scope">
            <el-tag v-for="method in scope.row.notifyMethods" :key="method">
              {{ method }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="severity_level" label="告警级别" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.status" active-text="启用" inactive-text="停用" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="text" size="small" @click="editAlarm(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="deleteAlarm(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>

    <!-- 创建/编辑告警对话框 -->
    <el-dialog :visible.sync="isDialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="告警名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入告警名称" />
        </el-form-item>
        <el-form-item label="策略组" prop="strategyGroup">
          <el-select v-model="formData.strategyGroup" placeholder="请选择策略组">
            <el-option v-for="group in strategyGroups" :key="group.id" :label="group.name" :value="group.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标对象" prop="target">
          <el-select v-model="formData.target" placeholder="请选择目标对象">
            <el-option v-for="host in managedHosts" :key="host.id" :label="host.name" :value="host.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收方式" prop="notificationMethod">
          <el-checkbox-group v-model="formData.notificationMethod">
            <el-checkbox label="邮件">邮件</el-checkbox>
            <el-checkbox label="短信">短信</el-checkbox>
            <el-checkbox label="系统通知">系统通知</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="告警级别" prop="severity_level">
          <el-select v-model="formData.severity_level" placeholder="请选择告警级别">
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
            <el-option label="严重" value="critical" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用" prop="status">
          <el-switch v-model="formData.status" active-text="启用" inactive-text="停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="isDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAlarm">保存</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";

// 模拟策略组数据
const strategyGroups = ref([
  { id: 1, name: "CPU 使用率告警策略组" },
  { id: 2, name: "内存使用率告警策略组" }
]);

// 模拟管理的主机数据
const managedHosts = ref([
  { id: 1, name: "服务器A" },
  { id: 2, name: "服务器B" },
  { id: 3, name: "服务器C" }
]);

// 模拟已有的告警策略数据
const alarmStrategies = ref([
  {
    id: 1,
    name: "CPU 高使用率告警",
    strategyGroupName: "CPU 使用率告警策略组",
    target: "服务器A",
    notifyMethods: ["邮件", "短信"],
    severity_level: "high",
    status: "active"
  },
  {
    id: 2,
    name: "内存不足告警",
    strategyGroupName: "内存使用率告警策略组",
    target: "服务器B",
    notifyMethods: ["系统通知"],
    severity_level: "medium",
    status: "inactive"
  }
]);

// 对话框显示状态与标题
const isDialogVisible = ref(false);
const dialogTitle = ref("");

// 表单数据
const formData = ref({
  id: null,
  name: "",
  strategyGroup: "",
  target: "",
  notificationMethod: [],
  severity_level: "medium",
  status: "active"
});

// 表单验证规则
const formRules = {
  name: [{ required: true, message: "告警名称不能为空", trigger: "blur" }],
  strategyGroup: [{ required: true, message: "请选择策略组", trigger: "change" }],
  target: [{ required: true, message: "请选择目标对象", trigger: "change" }]
};

// 打开新增对话框
const openCreateDialog = () => {
  resetForm();
  dialogTitle.value = "新增告警";
  isDialogVisible.value = true;
};

// 编辑告警
const editAlarm = (alarm) => {
  formData.value = { ...alarm };
  dialogTitle.value = "编辑告警";
  isDialogVisible.value = true;
};

// 保存告警
const saveAlarm = () => {
  const formRef = ref(null);
  formRef.value?.validate((valid) => {
    if (valid) {
      if (formData.value.id) {
        // 编辑逻辑
        const index = alarmStrategies.value.findIndex((a) => a.id === formData.value.id);
        if (index !== -1) alarmStrategies.value[index] = { ...formData.value };
        ElMessage.success("告警更新成功！");
      } else {
        // 新增逻辑
        formData.value.id = Date.now();
        alarmStrategies.value.push({ ...formData.value });
        ElMessage.success("告警创建成功！");
      }
      isDialogVisible.value = false;
    }
  });
};

// 删除告警
const deleteAlarm = (id) => {
  alarmStrategies.value = alarmStrategies.value.filter((a) => a.id !== id);
  ElMessage.success("告警已删除！");
};

// 重置表单
const resetForm = () => {
  formData.value = {
    id: null,
    name: "",
    strategyGroup: "",
    target: "",
    notificationMethod: [],
    severity_level: "medium",
    status: "active"
  };
};
</script>

<style scoped>
.alarm-container {
  height: 100%;
}

.alarm-header {
  padding: 10px 20px;
  background-color: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-main {
  padding: 20px;
}
</style>
