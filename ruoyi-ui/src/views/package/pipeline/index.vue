<template>
  <div class="app-container">
    <el-card>
      <div class="flex justify-between items-center mb-4">
        <h2>封装流水线管理</h2>
        <el-button type="primary" @click="dialogVisible = true">新增流水线</el-button>
      </div>

      <el-table :data="pipelines" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="流水线名称" />
        <el-table-column prop="image" label="基础镜像" />
        <el-table-column prop="resource" label="资源配置" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="handleDetail(scope.row)">详情</el-button>
            <el-button size="small" type="primary">编辑</el-button>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情 Drawer -->
    <el-drawer v-model="drawerVisible" title="流水线详情" size="40%">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="基础镜像">{{ detail.image }}</el-descriptions-item>
        <el-descriptions-item label="资源配置">{{ detail.resource }}</el-descriptions-item>
        <el-descriptions-item label="服务接口">{{ detail.api }}</el-descriptions-item>
        <el-descriptions-item label="健康检查">{{ detail.health }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <!-- 新增流水线 Dialog -->
    <el-dialog v-model="dialogVisible" title="新增流水线" width="600px">
      <el-steps :active="activeStep" finish-status="success" class="mb-4">
        <el-step title="基础镜像选择" />
        <el-step title="资源配置" />
        <el-step title="服务接口" />
        <el-step title="健康检查" />
      </el-steps>
      <div v-if="activeStep === 0">
        <el-select v-model="form.image" placeholder="请选择基础镜像">
          <el-option label="TensorFlow" value="TensorFlow" />
          <el-option label="PyTorch" value="PyTorch" />
          <el-option label="PaddlePaddle" value="PaddlePaddle" />
        </el-select>
      </div>
      <div v-if="activeStep === 1">
        <el-input v-model="form.resource" placeholder="如：CPU 2核, 内存 4G" />
      </div>
      <div v-if="activeStep === 2">
        <el-input v-model="form.api" placeholder="服务接口地址" />
      </div>
      <div v-if="activeStep === 3">
        <el-input v-model="form.health" placeholder="健康检查脚本/接口" />
      </div>
      <template #footer>
        <el-button @click="prevStep" :disabled="activeStep===0">上一步</el-button>
        <el-button v-if="activeStep<3" type="primary" @click="nextStep">下一步</el-button>
        <el-button v-else type="success" @click="savePipeline">完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from "vue";

const pipelines = ref([
  { id: 1, name: "NLP模型封装", image: "TensorFlow", resource: "2C4G", status: "已完成" },
  { id: 2, name: "CV模型封装", image: "PyTorch", resource: "4C8G", status: "运行中" },
]);

const drawerVisible = ref(false);
const detail = ref({});
const dialogVisible = ref(false);
const activeStep = ref(0);

const form = ref({ image: "", resource: "", api: "", health: "" });

const handleDetail = (row) => {
  detail.value = row;
  drawerVisible.value = true;
};

const nextStep = () => activeStep.value++;
const prevStep = () => activeStep.value--;
const savePipeline = () => {
  pipelines.value.push({
    id: pipelines.value.length + 1,
    name: "新流水线",
    ...form.value,
    status: "未运行",
  });
  dialogVisible.value = false;
  activeStep.value = 0;
  form.value = { image: "", resource: "", api: "", health: "" };
};
</script>
