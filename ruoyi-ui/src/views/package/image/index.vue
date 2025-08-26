<template>
  <div class="app-container">
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础镜像管理" name="base">
          <el-button type="primary" class="mb-4" @click="handleAdd">上传镜像</el-button>
          <el-table :data="baseImages" border style="width: 100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="镜像名称" />
            <el-table-column prop="framework" label="框架" />
            <el-table-column prop="version" label="版本" />
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" type="primary" @click="handleUpdateBase(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDeleteBase(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="服务镜像管理" name="service">
          <el-button type="primary" class="mb-4" @click="handleAdd">上传服务镜像</el-button>
          <el-table :data="serviceImages" border style="width: 100%">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="镜像名称" />
            <el-table-column prop="version" label="版本" />
            <el-table-column prop="tenant" label="租户" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small">版本</el-button>
                <el-button size="small" type="primary" @click="handleUpdateService(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDeleteService(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 上传/编辑镜像 Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form label-width="100px" ref="formRef" :model="form" :rules="rules">
        <el-form-item label="镜像名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="版本" prop="version">
          <el-input v-model="form.version" />
        </el-form-item>
        <el-form-item v-if="activeTab==='base'" label="框架" prop="framework">
          <el-select v-model="form.framework">
            <el-option label="TensorFlow" value="TensorFlow" />
            <el-option label="PyTorch" value="PyTorch" />
            <el-option label="PaddlePaddle" value="PaddlePaddle" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="activeTab==='service'" label="租户" prop="tenant">
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
import { ref, onMounted } from "vue";
import { listBaseImages, listServiceImages, addBaseImage, addServiceImage, updateBaseImage, updateServiceImage, delBaseImage, delServiceImage } from "@/api/package/image";

const activeTab = ref("base");

const baseImages = ref([]);
const serviceImages = ref([]);

const dialogVisible = ref(false);
const dialogTitle = ref("");
const form = ref({ id: undefined, name: "", version: "", framework: "", tenant: "" });

const rules = {
  name: [{ required: true, message: "镜像名称不能为空", trigger: "blur" }],
  version: [{ required: true, message: "版本不能为空", trigger: "blur" }],
  framework: [{ required: true, message: "框架不能为空", trigger: "blur" }],
  tenant: [{ required: true, message: "租户不能为空", trigger: "blur" }]
};

const formRef = ref(null);

// 获取基础镜像列表
const getBaseImageList = () => {
  listBaseImages().then(response => {
    baseImages.value = response.rows;
  });
};

// 获取服务镜像列表
const getServiceImageList = () => {
  listServiceImages().then(response => {
    serviceImages.value = response.rows;
  });
};

// 获取所有数据
const getList = () => {
  getBaseImageList();
  getServiceImageList();
};

// 新增按钮操作
const handleAdd = () => {
  form.value = { id: undefined, name: "", version: "", framework: "", tenant: "" };
  dialogTitle.value = activeTab.value === "base" ? "添加基础镜像" : "添加服务镜像";
  dialogVisible.value = true;
};

// 修改基础镜像按钮操作
const handleUpdateBase = (row) => {
  form.value = { ...row };
  dialogTitle.value = "修改基础镜像";
  dialogVisible.value = true;
};

// 修改服务镜像按钮操作
const handleUpdateService = (row) => {
  form.value = { ...row };
  dialogTitle.value = "修改服务镜像";
  dialogVisible.value = true;
};

// 删除基础镜像按钮操作
const handleDeleteBase = (row) => {
  const imageId = row.id;
  delBaseImage(imageId).then(response => {
    getBaseImageList();
  });
};

// 删除服务镜像按钮操作
const handleDeleteService = (row) => {
  const imageId = row.id;
  delServiceImage(imageId).then(response => {
    getServiceImageList();
  });
};

const saveImage = () => {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.value.id !== undefined) {
        // 修改
        if (activeTab.value === "base") {
          updateBaseImage(form.value).then(response => {
            getBaseImageList();
            dialogVisible.value = false;
          });
        } else {
          updateServiceImage(form.value).then(response => {
            getServiceImageList();
            dialogVisible.value = false;
          });
        }
      } else {
        // 新增
        if (activeTab.value === "base") {
          addBaseImage(form.value).then(response => {
            getBaseImageList();
            dialogVisible.value = false;
          });
        } else {
          addServiceImage(form.value).then(response => {
            getServiceImageList();
            dialogVisible.value = false;
          });
        }
      }
    }
  });
};

onMounted(() => {
  getList();
});
</script>
