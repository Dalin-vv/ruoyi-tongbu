<template>
  <div class="app-container">
   
    <!-- 主内容区域 -->
    <main class="main-content">
      <!-- 统计信息 -->
      <div class="stats">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalContainers }}</div>
          <div class="stat-label">总容器数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.runningContainers }}</div>
          <div class="stat-label">运行中容器</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalModels }}</div>
          <div class="stat-label">智能模型</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.accelerators }}</div>
          <div class="stat-label">AI加速卡</div>
        </div>
      </div>

    
      <!-- 标签页 -->
      <div class="tabs-container">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="资源编配" name="resource">
            <div class="table-container">
              <div class="table-actions">
                <div>
                  <el-input
                    placeholder="搜索容器名称"
                    v-model="resourceSearchQuery"
                    style="width: 200px;"
                    clearable
                  >
                    <template #prefix>
                      <i class="el-input__icon el-icon-search"></i>
                    </template>
                  </el-input>
                </div>
                <el-button type="primary" @click="dialogResourceVisible = true">添加资源限制</el-button>
              </div>
              
              <el-table :data="filteredResources" stripe border>
                <el-table-column prop="name" label="容器名称" min-width="120"></el-table-column>
                <el-table-column prop="cpu" label="CPU限制(核)" min-width="120"></el-table-column>
                <el-table-column prop="memory" label="内存限制(GB)" min-width="120"></el-table-column>
                <el-table-column prop="gpu" label="GPU限制(卡)" min-width="120"></el-table-column>
                <el-table-column prop="policy" label="策略" min-width="120">
                  <template #default="scope">
                    <el-tag :type="getPolicyTagType(scope.row.policy)" size="small">{{ scope.row.policy }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="状态" min-width="80">
                  <template #default="scope">
                    <span :class="scope.row.status === 'active' ? 'status-active' : 'status-inactive'">
                      {{ scope.row.status === 'active' ? '启用' : '禁用' }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                  <template #default="scope">
                    <el-button size="small" @click="editResource(scope.row)">编辑</el-button>
                    <el-button 
                      size="small" 
                      :type="scope.row.status === 'active' ? 'warning' : 'success'" 
                      @click="handleToggleResourceStatus(scope.row)"
                    >
                      {{ scope.row.status === 'active' ? '禁用' : '启用' }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="工作负载编配" name="workload">
            <div class="table-container">
              <div class="table-actions">
                <div>
                  <el-input
                    placeholder="搜索模型名称"
                    v-model="workloadSearchQuery"
                    style="width: 200px;"
                    clearable
                  >
                    <template #prefix>
                      <i class="el-input__icon el-icon-search"></i>
                    </template>
                  </el-input>
                </div>
                <el-button type="primary" @click="dialogWorkloadVisible = true">新建工作负载</el-button>
              </div>
              
              <el-table :data="filteredWorkloads" stripe border>
                <el-table-column prop="model" label="智能模型" min-width="150"></el-table-column>
                <el-table-column prop="accelerator" label="AI加速卡" min-width="120"></el-table-column>
                <el-table-column prop="template" label="资源模板" min-width="120"></el-table-column>
                <el-table-column label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.status === '运行中' ? 'success' : 'danger'" size="small">{{ scope.row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" min-width="150"></el-table-column>
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="scope">
                    <el-button 
                      size="small" 
                      :type="scope.row.status === '运行中' ? 'warning' : 'success'" 
                      @click="manageContainer(scope.row, scope.row.status === '运行中' ? 'stop' : 'start')"
                    >
                      {{ scope.row.status === '运行中' ? '停止' : '启动' }}
                    </el-button>
                    <el-button size="small" type="danger" @click="handleDeleteWorkload(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </main>

    

    <!-- 资源编配对话框 -->
    <el-dialog :title="isEditResource ? '编辑资源限制' : '添加资源限制'" v-model="dialogResourceVisible" width="600px">
      <el-form :model="currentResource" label-width="120px" :rules="resourceRules" ref="resourceFormRef">
        <el-form-item label="容器名称" prop="name">
          <el-input v-model="currentResource.name" placeholder="请输入容器名称"></el-input>
        </el-form-item>
        <el-form-item label="CPU限制(核)" prop="cpu">
          <el-input-number v-model="currentResource.cpu" :min="1" :max="128" controls-position="right"></el-input-number>
        </el-form-item>
        <el-form-item label="内存限制(GB)" prop="memory">
          <el-input-number v-model="currentResource.memory" :min="1" :max="256" controls-position="right"></el-input-number>
        </el-form-item>
        <el-form-item label="GPU限制(卡)" prop="gpu">
          <el-input-number v-model="currentResource.gpu" :min="0" :max="8" controls-position="right"></el-input-number>
        </el-form-item>
        <el-form-item label="策略" prop="policy">
          <el-select v-model="currentResource.policy" placeholder="请选择策略">
            <el-option label="高优先级" value="高优先级"></el-option>
            <el-option label="中优先级" value="中优先级"></el-option>
            <el-option label="低优先级" value="低优先级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="currentResource.status"
            active-value="active"
            inactive-value="inactive"
            active-text="启用"
            inactive-text="禁用"
          >
          </el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogResourceVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveResource">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 工作负载编配对话框 -->
    <el-dialog title="新建工作负载" v-model="dialogWorkloadVisible" width="600px">
      <el-form :model="currentWorkload" label-width="120px" :rules="workloadRules" ref="workloadFormRef">
        <el-form-item label="智能模型" prop="model">
          <el-select v-model="currentWorkload.model" placeholder="请选择智能模型">
            <el-option v-for="model in aiModels" :key="model" :label="model" :value="model"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="AI加速卡" prop="accelerator">
          <el-select v-model="currentWorkload.accelerator" placeholder="请选择AI加速卡">
            <el-option v-for="card in aiAccelerators" :key="card" :label="card" :value="card"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资源模板" prop="template">
          <el-select v-model="currentWorkload.template" placeholder="请选择资源模板">
            <el-option v-for="template in computeTemplates" :key="template.name" :label="template.name" :value="template.name"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogWorkloadVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveWorkload">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getAllocationStats, 
  listResources, 
  listWorkloads, 
  listAIModels, 
  listAIAccelerators,
  listComputeTemplates,
  addResource,
  updateResource,
  toggleResourceStatus,
  addWorkload,
  manageWorkloadStatus,
  deleteWorkload
} from '@/api/allocation/allocation'

// 响应式数据
const stats = reactive({
  totalContainers: 0,
  runningContainers: 0,
  totalModels: 0,
  accelerators: 0
})

const activeTab = ref('resource')
const resourceSearchQuery = ref('')
const workloadSearchQuery = ref('')

// 资源编配数据
const containerResources = ref([])

const dialogResourceVisible = ref(false)
const isEditResource = ref(false)
const currentResource = reactive({
  name: '',
  cpu: 2,
  memory: 4,
  gpu: 0,
  policy: '中优先级',
  status: 'active'
})

const resourceRules = {
  name: [{ required: true, message: '请输入容器名称', trigger: 'blur' }],
  cpu: [{ required: true, message: '请设置CPU限制', trigger: 'blur' }],
  memory: [{ required: true, message: '请设置内存限制', trigger: 'blur' }],
  policy: [{ required: true, message: '请选择策略', trigger: 'change' }]
}

// 工作负载编配数据
const workloads = ref([])

const aiModels = ref([])
const aiAccelerators = ref([])
const computeTemplates = ref([])

const dialogWorkloadVisible = ref(false)
const currentWorkload = reactive({
  model: '',
  accelerator: '',
  template: ''
})

const workloadRules = {
  model: [{ required: true, message: '请选择智能模型', trigger: 'change' }],
  accelerator: [{ required: true, message: '请选择AI加速卡', trigger: 'change' }],
  template: [{ required: true, message: '请选择资源模板', trigger: 'change' }]
}

// 表单引用
const resourceFormRef = ref(null)
const workloadFormRef = ref(null)

// 计算属性
const filteredResources = computed(() => {
  return containerResources.value.filter(resource => 
    resource.name.toLowerCase().includes(resourceSearchQuery.value.toLowerCase())
  )
})

const filteredWorkloads = computed(() => {
  return workloads.value.filter(workload => 
    workload.model.toLowerCase().includes(workloadSearchQuery.value.toLowerCase())
  )
})

// 生命周期钩子
onMounted(async () => {
  await loadInitialData()
})

// 方法
const loadInitialData = async () => {
  try {
    // 获取统计数据
    const statsResponse = await getAllocationStats()
    Object.assign(stats, statsResponse.data)
    
    // 获取资源列表
    const resourcesResponse = await listResources()
    containerResources.value = resourcesResponse.data
    
    // 获取工作负载列表
    const workloadsResponse = await listWorkloads()
    workloads.value = workloadsResponse.data
    
    // 获取AI模型列表
    const modelsResponse = await listAIModels()
    aiModels.value = modelsResponse.data
    
    // 获取AI加速卡列表
    const acceleratorsResponse = await listAIAccelerators()
    aiAccelerators.value = acceleratorsResponse.data
    
    // 获取计算模板列表
    const templatesResponse = await listComputeTemplates()
    computeTemplates.value = templatesResponse.data.map(name => ({ name }))
    
  } catch (error) {
    console.error('加载初始数据失败:', error)
    ElMessage.error('加载数据失败，请刷新页面重试')
  }
}

const getPolicyTagType = (policy) => {
  const typeMap = {
    '高优先级': 'danger',
    '中优先级': 'warning',
    '低优先级': 'info'
  }
  return typeMap[policy] || 'info'
}

// 资源编配方法
const editResource = (resource) => {
  Object.assign(currentResource, resource)
  isEditResource.value = true
  dialogResourceVisible.value = true
}

const handleToggleResourceStatus = async (resource) => {
  try {
    await toggleResourceStatus(resource.name, resource.status === 'active' ? 'inactive' : 'active')
    resource.status = resource.status === 'active' ? 'inactive' : 'active'
    ElMessage.success(`资源限制${resource.status === 'active' ? '启用' : '禁用'}成功`)
    // 重新加载数据以确保状态同步
    await loadInitialData()
  } catch (error) {
    console.error('切换资源状态失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const saveResource = async () => {
  try {
    await resourceFormRef.value.validate()
    
    if (isEditResource.value) {
      // 更新现有资源限制
      await updateResource(currentResource)
      ElMessage.success('资源限制更新成功')
    } else {
      // 添加新资源限制
      await addResource(currentResource)
      ElMessage.success('资源限制添加成功')
    }
    
    dialogResourceVisible.value = false
    resetCurrentResource()
    // 重新加载数据以获取最新状态
    await loadInitialData()
  } catch (error) {
    if (error.errors) {
      // 表单验证错误
      return
    }
    console.error('保存资源失败:', error)
    ElMessage.error('保存失败，请重试')
  }
}

const resetCurrentResource = () => {
  Object.assign(currentResource, {
    name: '',
    cpu: 2,
    memory: 4,
    gpu: 0,
    policy: '中优先级',
    status: 'active'
  })
  isEditResource.value = false
}

// 工作负载编配方法
const manageContainer = async (workload, action) => {
  const actionText = action === 'start' ? '启动' : '停止'
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}容器 "${workload.model}" 吗?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await manageWorkloadStatus(workload.id, action)
    workload.status = action === 'start' ? '运行中' : '已停止'
    ElMessage.success(`容器已${actionText}`)
    // 重新加载数据以确保状态同步
    await loadInitialData()
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消操作
      return
    }
    console.error('管理容器状态失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const handleDeleteWorkload = async (workload) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除工作负载 "${workload.model}" 吗?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await deleteWorkload(workload.id)
    ElMessage.success('删除成功')
    // 重新加载数据以获取最新状态
    await loadInitialData()
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消操作
      return
    }
    console.error('删除工作负载失败:', error)
    ElMessage.error('删除失败，请重试')
  }
}

const saveWorkload = async () => {
  try {
    await workloadFormRef.value.validate()
    
    await addWorkload(currentWorkload)
    ElMessage.success('工作负载添加成功')
    
    dialogWorkloadVisible.value = false
    Object.assign(currentWorkload, {
      model: '',
      accelerator: '',
      template: ''
    })
    // 重新加载数据以获取最新状态
    await loadInitialData()
  } catch (error) {
    if (error.errors) {
      // 表单验证错误
      return
    }
    console.error('保存工作负载失败:', error)
    ElMessage.error('保存失败，请重试')
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", Arial, sans-serif;
  background-color: #f5f7fa;
  color: #333;
  line-height: 1.5;
}
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.header {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: white;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo {
  display: flex;
  align-items: center;
}
.logo i {
  font-size: 28px;
  margin-right: 10px;
}
.logo h1 {
  font-size: 22px;
  font-weight: 600;
}
.main-content {
  flex: 1;
  padding: 20px;
  max-width: 1800px;
  margin: 0 auto;
  width: 100%;
}
.page-title {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.page-title h2 {
  font-size: 20px;
  font-weight: 500;
  color: #303133;
}
.stats {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}
.stat-card {
  padding: 15px;
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: center;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
  margin-bottom: 5px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.tabs-container {
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}
.table-container {
  padding: 20px;
}
.table-actions {
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
}
.status-active {
  color: #67c23a;
}
.status-inactive {
  color: #f56c6c;
}
/* 按钮样式优化 */
.table-actions .el-button,
.el-table .el-button {
  min-width: 80px;
}
/* 添加资源按钮特殊样式 */
.table-actions .el-button.el-button--primary {
  min-width: 120px;
}
.dialog-footer .el-button {
  min-width: 100px;
}
.footer {
  text-align: center;
  padding: 20px;
  color: #909399;
  font-size: 14px;
  margin-top: auto;
}
@media (max-width: 768px) {
  .stats {
    grid-template-columns: 1fr 1fr;
  }
  .table-actions {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
