<template>
  <el-container class="strategy-group-container">
    <!-- 页面头部 -->
    <el-header class="strategy-group-header">
      <h2>策略组管理</h2>
      <el-button type="primary" @click="openAddDialog">新增策略组</el-button>
    </el-header>

    <!-- 页面内容 -->
    <el-main class="strategy-group-content">
      <el-table :data="strategyGroups" border style="width: 100%">
        <el-table-column prop="id" label="编号" width="80"></el-table-column>
        <el-table-column prop="groupName" label="策略组名称"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column prop="createdBy" label="创建者ID" width="150"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="editStrategyGroup(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteStrategyGroup(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>

    <!-- 新增/编辑策略组的对话框 -->
    <el-dialog :title="isEditing ? '编辑策略组' : '新增策略组'" v-model="dialogVisible" width="500px">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="策略组名称" prop="groupName">
          <el-input v-model="formData.groupName" placeholder="请输入策略组名称"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" placeholder="请输入描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveStrategyGroup">保存</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import axios from "axios";

const strategyGroups = ref([]);
const dialogVisible = ref(false);
const formRef = ref(null);
const formData = ref({
  id: null,
  groupName: "",
  description: "",
  createdBy: null,
  alarmCondition: {}
});
const formRules = {
  groupName: [{ required: true, message: "策略组名称不能为空", trigger: "blur" }],
  description: [{ required: true, message: "描述不能为空", trigger: "blur" }],
};
const isEditing = ref(false);

// 加载策略组数据
const loadStrategyGroups = async () => {
  try {
    const response = await axios.get("/api/groups");
    strategyGroups.value = response.data.data;
  } catch (error) {
    ElMessage.error("加载策略组失败");
  }
};

// 打开新增对话框
const openAddDialog = () => {
  resetForm();
  dialogVisible.value = true;
};

// 打开编辑对话框
const editStrategyGroup = (group) => {
  formData.value = JSON.parse(JSON.stringify(group));
  isEditing.value = true;
  dialogVisible.value = true;
};

// 保存策略组
const saveStrategyGroup = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEditing.value) {
          await axios.put("/api/groups", formData.value);
        } else {
          await axios.post("/api/groups", formData.value);
        }
        ElMessage.success("策略组保存成功");
        dialogVisible.value = false;
        loadStrategyGroups();
      } catch (error) {
        ElMessage.error("保存策略组失败");
      }
    }
  });
};

// 删除策略组
const deleteStrategyGroup = async (group) => {
  try {
    await axios.delete(`/api/groups/${group.id}`);
    ElMessage.success("策略组删除成功");
    loadStrategyGroups();
  } catch (error) {
    ElMessage.error("删除策略组失败");
  }
};

// 重置表单
const resetForm = () => {
  formData.value = {
    id: null,
    groupName: "",
    description: "",
    createdBy: null,
    alarmCondition: {}
  };
  isEditing.value = false;
};

// 初始化加载
onMounted(loadStrategyGroups);
</script>

<style scoped>
.strategy-group-container {
  height: 100%;
}

.strategy-group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color);
}

.strategy-group-content {
  padding: 20px;
}
</style>