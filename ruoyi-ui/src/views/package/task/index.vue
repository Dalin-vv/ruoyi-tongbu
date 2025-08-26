<template>
  <div class="app-container">
    <el-card>
      <div class="flex justify-between items-center mb-4">
        <h2>封装任务管理</h2>
        <el-button type="primary" @click="handleAdd">新增任务</el-button>
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
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增/编辑任务 Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form label-width="100px" ref="formRef" :model="form" :rules="rules">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="执行方式" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="手动">手动</el-radio>
            <el-radio label="定时">定时</el-radio>
            <el-radio label="事件">事件</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.type==='定时'" label="Cron 表达式" prop="cron">
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
import { ref, onMounted } from "vue";
import { listTasks, addTask, updateTask, delTask, executeTask } from "@/api/package/task";

const tasks = ref([]);

const drawerVisible = ref(false);
const logs = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref("");
const form = ref({ id: undefined, name: "", type: "手动", cron: "" });

const rules = {
  name: [{ required: true, message: "任务名称不能为空", trigger: "blur" }],
  type: [{ required: true, message: "执行方式不能为空", trigger: "blur" }],
  cron: [{ required: true, message: "Cron表达式不能为空", trigger: "blur" }]
};

const formRef = ref(null);

// 获取任务列表
const getTaskList = () => {
  listTasks().then(response => {
    tasks.value = response.rows;
  });
};

// 新增按钮操作
const handleAdd = () => {
  form.value = { id: undefined, name: "", type: "手动", cron: "" };
  dialogTitle.value = "添加任务";
  dialogVisible.value = true;
};

// 修改按钮操作
const handleUpdate = (row) => {
  form.value = { ...row };
  dialogTitle.value = "修改任务";
  dialogVisible.value = true;
};

// 删除按钮操作
const handleDelete = (row) => {
  const taskId = row.id;
  delTask(taskId).then(response => {
    getTaskList();
  });
};

// 执行按钮操作
const handleExecute = (row) => {
  const taskId = row.id;
  executeTask(taskId).then(response => {
    getTaskList();
  });
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
  formRef.value.validate(valid => {
    if (valid) {
      if (form.value.id !== undefined) {
        // 修改
        updateTask(form.value).then(response => {
          getTaskList();
          dialogVisible.value = false;
        });
      } else {
        // 新增
        addTask(form.value).then(response => {
          getTaskList();
          dialogVisible.value = false;
        });
      }
    }
  });
};

onMounted(() => {
  getTaskList();
});
</script>
