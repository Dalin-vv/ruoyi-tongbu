<template>
  <div class="app-container">
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础镜像管理" name="base">
          <el-button type="primary" class="mb-4" @click="dialogVisible = true">上传镜像</el-button>
          <el-table :data="baseImages" border style="width: 100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="镜像名称" />
            <el-table-column prop="framework" label="框架" />
            <el-table-column prop="version" label="版本" />
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" type="primary">编辑</el-button>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="服务镜像管理" name="service">
          <el-button type="primary" class="mb-4" @click="dialogVisible = true">上传服务镜像</el-button>
          <el-table :data="serviceImages" border style="width: 100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="镜像名称" />
            <el-table-column prop="version" label="版本" />
            <el-table-column prop="tenant" label="租户" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small">版本</el-button>
                <el-button size="small" type="primary">编辑</el-button>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 上传镜像 Dialog -->
    <el-dialog v-model="dialogVisible" title="上传镜像" width="500px">
      <el-form label-width="100px">
        <el-form-item label="镜像名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="版本">
          <el-input v-model="form.version" />
        </el-form-item>
        <el-form-item v-if="activeTab==='base'" label="框架">
          <el-select v-model="form.framework">
            <el-option label="TensorFlow" value="TensorFlow" />
            <el-option label="PyTorch" value="PyTorch" />
            <el-option label="PaddlePaddle" value="PaddlePaddle" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="activeTab==='service'" label="租户">
          <el-input v-model="form.tenant" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveImage">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from "vue";

const activeTab = ref("base");

const baseImages = ref([
  { id: 1, name: "tf-base", framework: "TensorFlow", version: "2.8" },
  { id: 2, name: "pytorch-base", framework: "PyTorch", version: "1.12" },
]);

const serviceImages = ref([
  { id: 1, name: "cv-service", version: "v1.0", tenant: "租户A" },
  { id: 2, name: "nlp-service", version: "v2.1", tenant: "租户B" },
]);

const dialogVisible = ref(false);
const form = ref({ name: "", version: "", framework: "", tenant: "" });

const saveImage = () => {
  if (activeTab.value === "base") {
    baseImages.value.push({
      id: baseImages.value.length + 1,
      name: form.value.name,
      framework: form.value.framework,
      version: form.value.version,
    });
  } else {
    serviceImages.value.push({
      id: serviceImages.value.length + 1,
      name: form.value.name,
      version: form.value.version,
      tenant: form.value.tenant,
    });
  }
  dialogVisible.value = false;
  form.value = { name: "", version: "", framework: "", tenant: "" };
};
</script>
