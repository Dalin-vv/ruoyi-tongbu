<template>
  <div class="app-container">
    <el-card>
      <div class="flex justify-between items-center mb-4">
        <h2>封装流水线管理</h2>
        <el-button type="primary" @click="handleAdd">新增流水线</el-button>
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
            <el-button size="small" type="primary" @click="handleUpdate(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增/编辑流水线 Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form label-width="100px" ref="formRef" :model="form" :rules="rules">
        <el-steps :active="activeStep" finish-status="success" class="mb-4">
          <el-step title="基础镜像选择" />
          <el-step title="资源配置" />
          <el-step title="服务接口" />
          <el-step title="健康检查" />
        </el-steps>
        <div v-if="activeStep === 0">
          <el-form-item label="流水线名称" prop="name">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="基础镜像" prop="image">
            <el-select v-model="form.image" placeholder="请选择基础镜像">
              <el-option label="TensorFlow" value="TensorFlow" />
              <el-option label="PyTorch" value="PyTorch" />
              <el-option label="PaddlePaddle" value="PaddlePaddle" />
            </el-select>
          </el-form-item>
        </div>
        <div v-if="activeStep === 1">
          <el-form-item label="资源配置" prop="resource">
            <el-input v-model="form.resource" placeholder="如：CPU 2核, 内存 4G" />
          </el-form-item>
        </div>
        <div v-if="activeStep === 2">
          <el-form-item label="服务接口" prop="api">
            <el-input v-model="form.api" placeholder="服务接口地址" />
          </el-form-item>
        </div>
        <div v-if="activeStep === 3">
          <el-form-item label="健康检查" prop="health">
            <el-input v-model="form.health" placeholder="健康检查脚本/接口" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="prevStep" :disabled="activeStep===0">上一步</el-button>
        <el-button v-if="activeStep<3" type="primary" @click="nextStep">下一步</el-button>
        <el-button v-else type="success" @click="savePipeline">完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { listPipelines, addPipeline, updatePipeline, delPipeline } from "@/api/package/pipeline";

const pipelines = ref([]);

const drawerVisible = ref(false);
const detail = ref({});
const dialogVisible = ref(false);
const dialogTitle = ref("");
const activeStep = ref(0);

const form = ref({ id: undefined, name: "", image: "", resource: "", api: "", health: "" });

const rules = {
  name: [{ required: true, message: "流水线名称不能为空", trigger: "blur" }],
  image: [{ required: true, message: "基础镜像不能为空", trigger: "blur" }],
  resource: [{ required: true, message: "资源配置不能为空", trigger: "blur" }],
  api: [{ required: true, message: "服务接口不能为空", trigger: "blur" }],
  health: [{ required: true, message: "健康检查不能为空", trigger: "blur" }]
};

const formRef = ref(null);

// 获取流水线列表
const getPipelineList = () => {
  listPipelines().then(response => {
    pipelines.value = response.rows;
  });
};

// 新增按钮操作
const handleAdd = () => {
  form.value = { id: undefined, name: "", image: "", resource: "", api: "", health: "" };
  dialogTitle.value = "添加流水线";
  activeStep.value = 0;
  dialogVisible.value = true;
};

// 修改按钮操作
const handleUpdate = (row) => {
  form.value = { ...row };
  dialogTitle.value = "修改流水线";
  activeStep.value = 0;
  dialogVisible.value = true;
};

// 删除按钮操作
const handleDelete = (row) => {
  const pipelineId = row.id;
  delPipeline(pipelineId).then(response => {
    getPipelineList();
  });
};

const handleDetail = (row) => {
  detail.value = row;
  drawerVisible.value = true;
};

const nextStep = () => activeStep.value++;
const prevStep = () => activeStep.value--;

const savePipeline = () => {
  if (activeStep.value < 3) {
    nextStep();
    return;
  }

  formRef.value.validate(valid => {
    if (valid) {
      if (form.value.id !== undefined) {
        // 修改
        updatePipeline(form.value).then(response => {
          getPipelineList();
          dialogVisible.value = false;
        });
      } else {
        // 新增
        addPipeline(form.value).then(response => {
          getPipelineList();
          dialogVisible.value = false;
        });
      }
    }
  });
};

onMounted(() => {
  getPipelineList();
});
</script>
