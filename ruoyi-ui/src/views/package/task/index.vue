<template>
  <div class="app-container">
    <el-card>
      <div class="flex justify-between items-center mb-4">
        <h2>封装任务管理</h2>
        <el-button type="primary" @click="dialogVisible = true">新增任务</el-button>
      </div>

      <el-table :data="tasks" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="任务名称" />
        <el-table-column prop="type" label="执行方式" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" @click="handleExecute(scope.row)">执行</el-button>
            <el-button size="small" @click="handleLog(scope.row)">日志</el-button>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 日志 Drawer -->
    <el-drawer v-model="drawerVisible" title="任务日志" size="40%">
      <el-scrollbar height="300px">
        <p v-for="(line,i) in logs" :key="i">{{ line }}</p>
      </el-scrollbar>
    </el-drawer>

    <!-- 新增任务 Dialog -->
    <el-dialog v-model="dialogVisible" title="新增任务" width="500px">
      <el-form label-width="100px">
        <el-form-item label="任务名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="执行方式">
          <el-radio-group v-model="form.type">
            <el-radio label="手动">手动</el-radio>
            <el-radio label="定时">定时</el-radio>
            <el-radio label="事件">事件</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.type==='定时'" label="Cron 表达式">
          <el-input v-model="form.cron" placeholder="0 0 * * * ?" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveTask">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from "vue";

const tasks = ref([
  { id: 1, name: "NLP流水线执行", type: "手动", status: "已完成" },
  { id: 2, name: "CV流水线定时任务", type: "定时", status: "运行中" },
]);

const drawerVisible = ref(false);
const logs = ref([]);
const dialogVisible = ref(false);
const form = ref({ name: "", type: "手动", cron: "" });

const handleExecute = (row) => {
  row.status = "运行中";
};

const handleLog = (row) => {
  logs.value = [
    `任务 ${row.name} 开始执行...`,
    "拉取镜像...",
    "封装流水线运行中...",
    "任务完成。",
  ];
  drawerVisible.value = true;
};

const saveTask = () => {
  tasks.value.push({
    id: tasks.value.length + 1,
    ...form.value,
    status: "未运行",
  });
  dialogVisible.value = false;
  form.value = { name: "", type: "手动", cron: "" };
};
</script>
