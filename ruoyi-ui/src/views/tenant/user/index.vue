<template>
  <div>
    <div class="tenant-management-container">
      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 内容区域 -->
        <div class="content">
          <!-- 操作卡片 -->
          <el-card class="operation-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">租户操作</span>
              </div>
            </template>
            <el-button type="primary" @click="showModal = true">创建新租户</el-button>
            <el-button>批量操作</el-button>
          </el-card>
          
          <!-- 租户列表 -->
          <el-card class="tenant-list-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">租户列表</span>
                <el-input
                  v-model="searchQuery"
                  placeholder="搜索租户..."
                  style="width: 250px"
                  clearable
                >
                  <template #prefix>
                    <el-icon><search /></el-icon>
                  </template>
                </el-input>
              </div>
            </template>
            
            <el-table :data="filteredTenants" style="width: 100%">
              <el-table-column type="selection" width="55"></el-table-column>
              <el-table-column prop="name" label="租户名称"></el-table-column>
              <el-table-column label="联系人">
                <template #default="scope">
                  {{ scope.row.contact }} ({{ scope.row.email }})
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间"></el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="scope">
                  <el-tag
                    :type="getStatusType(scope.row.status)"
                    :effect="getStatusEffect(scope.row.status)"
                  >
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="资源配额">
                <template #default="scope">
                  CPU: {{ scope.row.cpu }}核 / 内存: {{ scope.row.memory }}GB
                </template>
              </el-table-column>
              <el-table-column label="生命周期操作" width="380">
                <template #default="scope">
                  <el-button-group>
                    <el-button
                      v-if="scope.row.status === TENANT_STATUS.CREATED || scope.row.status === TENANT_STATUS.INACTIVE"
                      size="small"
                      type="success"
                      @click="activateTenant(scope.row.id)"
                    >
                      激活
                    </el-button>
                    
                    <el-button
                      v-if="scope.row.status === TENANT_STATUS.ACTIVE"
                      size="small"
                      type="warning"
                      @click="suspendTenant(scope.row.id)"
                    >
                      暂停
                    </el-button>
                    
                    <el-button
                      v-if="scope.row.status === TENANT_STATUS.SUSPENDED"
                      size="small"
                      type="success"
                      @click="resumeTenant(scope.row.id)"
                    >
                      恢复
                    </el-button>
                    
                    <el-button
                      v-if="[TENANT_STATUS.ACTIVE, TENANT_STATUS.SUSPENDED].includes(scope.row.status)"
                      size="small"
                      type="danger"
                      @click="disableTenant(scope.row.id)"
                    >
                      停用
                    </el-button>
                    
                    <el-button
                      v-if="scope.row.status === TENANT_STATUS.DISABLED"
                      size="small"
                      type="info"
                      @click="deleteTenant(scope.row.id)"
                    >
                      删除
                    </el-button>
                  </el-button-group>
                  
                  <el-button
                    size="small"
                    type="primary"
                    @click="showTenantDetail(scope.row)"
                    style="margin-left: 10px"
                  >
                    详情
                  </el-button>
                  
                  <el-button
                    size="small"
                    @click="editTenant(scope.row)"
                    style="margin-left: 10px"
                  >
                    编辑
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </div>
    </div>
  
  <!-- 创建租户模态框 -->
  <el-dialog
    v-model="showModal"
    title="创建新租户"
    width="600px"
    :before-close="handleClose"
  >
    <el-form :model="newTenant" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="租户名称">
            <el-input v-model="newTenant.name" placeholder="输入租户名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="租户ID">
            <el-input :value="generateTenantID()" placeholder="自动生成" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="联系人">
        <el-input v-model="newTenant.contact" placeholder="输入联系人姓名"></el-input>
      </el-form-item>
      
      <el-form-item label="联系邮箱">
        <el-input v-model="newTenant.email" placeholder="输入联系邮箱"></el-input>
      </el-form-item>
      
      <el-form-item label="租户描述">
        <el-input v-model="newTenant.description" placeholder="输入租户描述信息"></el-input>
      </el-form-item>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="CPU限制(核)">
            <el-input-number v-model="newTenant.cpu" :min="0" :step="1" placeholder="例如: 4"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="内存限制(GB)">
            <el-input-number v-model="newTenant.memory" :min="0" :step="1" placeholder="例如: 8"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
      <el-col :span="12">
        <el-form-item label="初始状态">
        <el-select v-model="newTenant.status" placeholder="请选择状态">
          <el-option label="激活" :value="TENANT_STATUS.ACTIVE"></el-option>
          <el-option label="未激活" :value="TENANT_STATUS.INACTIVE"></el-option>
          <el-option label="新创建" :value="TENANT_STATUS.CREATED"></el-option>
        </el-select>
      </el-form-item>
      </el-col>
      
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="createTenant">创建租户</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 编辑租户模态框 -->
  <el-dialog
    v-model="showEditModal"
    title="编辑租户信息"
    width="600px"
    :before-close="handleEditClose"
  >
    <el-form :model="editTenantForm" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="租户名称">
            <el-input v-model="editTenantForm.name" placeholder="输入租户名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="租户ID">
            <el-input :value="editTenantForm.id" disabled></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="联系人">
        <el-input v-model="editTenantForm.contact" placeholder="输入联系人姓名"></el-input>
      </el-form-item>
      
      <el-form-item label="联系邮箱">
        <el-input v-model="editTenantForm.email" placeholder="输入联系邮箱"></el-input>
      </el-form-item>
      
      <el-form-item label="租户描述">
        <el-input 
          v-model="editTenantForm.description" 
          placeholder="输入租户描述信息"
          type="textarea"
          :rows="3"
        ></el-input>
      </el-form-item>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="CPU限制(核)">
            <el-input-number v-model="editTenantForm.cpu" :min="0" :step="1" placeholder="例如: 4"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="内存限制(GB)">
            <el-input-number v-model="editTenantForm.memory" :min="0" :step="1" placeholder="例如: 8"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="状态">
        <el-select v-model="editTenantForm.status" placeholder="请选择状态">
          <el-option label="激活" :value="TENANT_STATUS.ACTIVE"></el-option>
          <el-option label="未激活" :value="TENANT_STATUS.INACTIVE"></el-option>
          <el-option label="已暂停" :value="TENANT_STATUS.SUSPENDED"></el-option>
          <el-option label="已停用" :value="TENANT_STATUS.DISABLED"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showEditModal = false">取消</el-button>
        <el-button type="primary" @click="updateTenant">保存修改</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 租户详情对话框 -->
  <el-dialog
    v-model="showDetailDialog"
    title="租户详情"
    width="800px"
  >
    <el-descriptions
      :column="2"
      border
    >
      <el-descriptions-item label="租户名称">{{ currentTenant.name }}</el-descriptions-item>
      <el-descriptions-item label="租户状态">
        <el-tag :type="getStatusType(currentTenant.status)">
          {{ getStatusText(currentTenant.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="联系人">{{ currentTenant.contact }}</el-descriptions-item>
      <el-descriptions-item label="联系邮箱">{{ currentTenant.email }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ currentTenant.createTime }}</el-descriptions-item>
      <el-descriptions-item label="最后修改时间">{{ currentTenant.updateTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="资源配额" :span="2">
        CPU: {{ currentTenant.cpu }}核 / 内存: {{ currentTenant.memory }}GB
      </el-descriptions-item>
      <el-descriptions-item label="租户描述" :span="2">
        {{ currentTenant.description || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="生命周期日志" :span="2">
        <el-timeline v-if="currentTenant.lifecycle && currentTenant.lifecycle.length">
          <el-timeline-item
            v-for="(activity, index) in currentTenant.lifecycle"
            :key="index"
            :timestamp="activity.time"
            :type="activity.type"
          >
            {{ activity.content }}
          </el-timeline-item>
        </el-timeline>
        <span v-else>暂无生命周期记录</span>
      </el-descriptions-item>
    </el-descriptions>
  </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

// 定义租户状态常量
const TENANT_STATUS = {
  CREATED: 'created',    
  ACTIVE: 'active',      
  INACTIVE: 'inactive',  
  SUSPENDED: 'suspended', 
  DISABLED: 'disabled',  
  DELETED: 'deleted'     
}

export default {
  name: 'TenantManagement',
  components: {
    Search
  },
  setup() {
    // 状态管理
    const showModal = ref(false)
    const showEditModal = ref(false)
    const searchQuery = ref('')
    
    // 租户数据
    const tenants = ref([
      {
        id: 1,
        name: 'tenant-production',
        contact: '张三',
        email: 'zhangsan@example.com',
        createTime: '2023-05-10',
        updateTime: '2023-05-10',
        status: TENANT_STATUS.ACTIVE,
        cpu: 16,
        memory: 32,
        description: '',
        lifecycle: [
          {
            time: '2023-05-10 10:00:00',
            type: 'primary',
            content: '租户创建'
          },
          {
            time: '2023-05-10 10:05:00',
            type: 'success',
            content: '租户激活'
          }
        ]
      },
      {
        id: 2,
        name: 'tenant-development',
        contact: '李四',
        email: 'lisi@example.com',
        createTime: '2023-06-15',
        status: TENANT_STATUS.ACTIVE,
        cpu: 8,
        memory: 16,
        description: ''
      },
      {
        id: 3,
        name: 'tenant-testing',
        contact: '王五',
        email: 'wangwu@example.com',
        createTime: '2023-07-22',
        status: TENANT_STATUS.INACTIVE,
        cpu: 4,
        memory: 8,
        description: ''
      },
      {
        id: 4,
        name: 'tenant-archive',
        contact: '赵六',
        email: 'zhaoliu@example.com',
        createTime: '2023-02-14',
        status: TENANT_STATUS.DISABLED,
        cpu: 0,
        memory: 0,
        description: ''
      }
    ])
    
    // 新租户表单
    const newTenant = reactive({
      name: '',
      contact: '',
      email: '',
      description: '',
      cpu: 0,
      memory: 0,
      status: TENANT_STATUS.ACTIVE
    })
    
    // 编辑租户表单
    const editTenantForm = reactive({
      id: null,
      name: '',
      contact: '',
      email: '',
      description: '',
      cpu: 0,
      memory: 0,
      status: TENANT_STATUS.ACTIVE
    })
    
    // 计算属性
    const filteredTenants = computed(() => {
      if (!searchQuery.value) {
        return tenants.value
      }
      const query = searchQuery.value.toLowerCase()
      return tenants.value.filter(tenant => 
        tenant.name.toLowerCase().includes(query) ||
        tenant.contact.toLowerCase().includes(query) ||
        tenant.email.toLowerCase().includes(query)
      )
    })
    
    // 方法
    const generateTenantID = () => {
      return 'tenant-' + Math.random().toString(36).substr(2, 9)
    }

    const getStatusText = (status) => {
      const statusMap = {
        [TENANT_STATUS.CREATED]: '新创建',
        [TENANT_STATUS.ACTIVE]: '运行中',
        [TENANT_STATUS.INACTIVE]: '未激活',
        [TENANT_STATUS.SUSPENDED]: '已暂停',
        [TENANT_STATUS.DISABLED]: '已停用',
        [TENANT_STATUS.DELETED]: '已删除'
      }
      return statusMap[status] || '未知状态'
    }

    const getStatusType = (status) => {
      const typeMap = {
        [TENANT_STATUS.CREATED]: 'info',
        [TENANT_STATUS.ACTIVE]: 'success', 
        [TENANT_STATUS.INACTIVE]: 'warning',
        [TENANT_STATUS.SUSPENDED]: 'warning',
        [TENANT_STATUS.DISABLED]: 'danger',
        [TENANT_STATUS.DELETED]: 'info'
      }
      return typeMap[status] || 'info'
    }

    const getStatusEffect = (status) => {
      return [TENANT_STATUS.CREATED, TENANT_STATUS.DELETED].includes(status) ? 'plain' : 'light'
    }
    
    const createTenant = () => {
      const newId = Math.max(...tenants.value.map(t => t.id), 0) + 1
      const currentDate = new Date().toISOString().split('T')[0]
      
      tenants.value.push({
        id: newId,
        name: newTenant.name,
        contact: newTenant.contact,
        email: newTenant.email,
        createTime: currentDate,
        status: newTenant.status,
        cpu: newTenant.cpu,
        memory: newTenant.memory,
        description: newTenant.description
      })
      
      // 重置表单
      Object.assign(newTenant, {
        name: '',
        contact: '',
        email: '',
        description: '',
        cpu: 0,
        memory: 0,
        status: 'active'
      })
      
      showModal.value = false
      ElMessage.success('租户创建成功')
    }
    
    const editTenant = (tenant) => {
      // 填充编辑表单数据
      Object.assign(editTenantForm, {
        id: tenant.id,
        name: tenant.name,
        contact: tenant.contact,
        email: tenant.email,
        description: tenant.description,
        cpu: tenant.cpu,
        memory: tenant.memory,
        status: tenant.status
      })
      showEditModal.value = true
    }
    
    const updateTenant = () => {
      const index = tenants.value.findIndex(t => t.id === editTenantForm.id)
      if (index !== -1) {
        tenants.value[index] = {
          ...tenants.value[index],
          name: editTenantForm.name,
          contact: editTenantForm.contact,
          email: editTenantForm.email,
          description: editTenantForm.description,
          cpu: editTenantForm.cpu,
          memory: editTenantForm.memory,
          status: editTenantForm.status
        }
        showEditModal.value = false
        ElMessage.success('租户信息更新成功')
      }
    }
    
    const handleEditClose = (done) => {
      ElMessageBox.confirm('确定要关闭吗？表单数据将不会保存。')
        .then(() => {
          done()
        })
        .catch(() => {
          // 取消关闭
        })
    }
    
    // 租户详情对话框
    const showDetailDialog = ref(false)
    const currentTenant = ref({})
    
    const showTenantDetail = (tenant) => {
      currentTenant.value = tenant
      showDetailDialog.value = true
    }
    
    const activateTenant = async (id) => {
      try {
        await ElMessageBox.confirm('确定要激活该租户吗？', '提示')
        const tenant = tenants.value.find(t => t.id === id)
        if (tenant) {
          tenant.status = TENANT_STATUS.ACTIVE
          addLifecycleRecord(tenant, '租户激活')
          ElMessage.success('租户已激活')
        }
      } catch (error) {
        // 用户取消操作
      }
    }
    
    const suspendTenant = async (id) => {
      try {
        await ElMessageBox.confirm('确定要暂停该租户吗？', '警告', {
          type: 'warning'
        })
        const tenant = tenants.value.find(t => t.id === id)
        if (tenant) {
          tenant.status = TENANT_STATUS.SUSPENDED
          addLifecycleRecord(tenant, '租户暂停')
          ElMessage.warning('租户已暂停')
        }
      } catch (error) {
        // 用户取消操作
      }
    }
    
    const resumeTenant = async (id) => {
      try {
        await ElMessageBox.confirm('确定要恢复该租户吗？', '提示')
        const tenant = tenants.value.find(t => t.id === id)
        if (tenant) {
          tenant.status = TENANT_STATUS.ACTIVE
          addLifecycleRecord(tenant, '租户恢复运行')
          ElMessage.success('租户已恢复')
        }
      } catch (error) {
        // 用户取消操作
      }
    }
    
    const disableTenant = async (id) => {
      try {
        await ElMessageBox.confirm('确定要停用该租户吗？此操作将终止租户所有服务！', '警告', {
          type: 'warning'
        })
        const tenant = tenants.value.find(t => t.id === id)
        if (tenant) {
          tenant.status = TENANT_STATUS.DISABLED
          addLifecycleRecord(tenant, '租户停用')
          ElMessage.warning('租户已停用')
        }
      } catch (error) {
        // 用户取消操作
      }
    }
    
    const deleteTenant = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个租户吗？此操作不可恢复。', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        const tenant = tenants.value.find(t => t.id === id)
        if (tenant) {
          tenant.status = TENANT_STATUS.DELETED
          addLifecycleRecord(tenant, '租户删除')
          tenants.value = tenants.value.filter(t => t.id !== id)
          ElMessage.success('租户已删除')
        }
      } catch (error) {
        // 用户取消删除
      }
    }
    
    const addLifecycleRecord = (tenant, action) => {
      if (!tenant.lifecycle) {
        tenant.lifecycle = []
      }
      tenant.lifecycle.push({
        time: new Date().toLocaleString(),
        type: getStatusType(tenant.status),
        content: action
      })
    }
    
    
    const handleClose = (done) => {
      ElMessageBox.confirm('确定要关闭吗？表单数据将不会保存。')
        .then(() => {
          done()
        })
        .catch(() => {
          // 取消关闭
        })
    }
    
    return {
      showModal,
      showEditModal,
      searchQuery,
      tenants,
      newTenant,
      editTenantForm,
      filteredTenants,
      generateTenantID,
      getStatusText,
      getStatusType,
      getStatusEffect,
      createTenant,
      editTenant,
      updateTenant,
      activateTenant,
      suspendTenant,
      resumeTenant,
      disableTenant,
      deleteTenant,
      handleClose,
      handleEditClose,
      TENANT_STATUS,
      showDetailDialog,
      currentTenant,
      showTenantDetail
    }
  }
}
</script>

<style scoped>
.tenant-management-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
  color: #333;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}



.tenant-management-container .logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #e9ecef;
}

.tenant-management-container .nav-menu {
  list-style: none;
  padding: 10px 0;
}

.tenant-management-container .nav-item {
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
}

.tenant-management-container .nav-item:hover {
  background-color: #f8f9fa;
}

.tenant-management-container .nav-item.active {
  background-color: #326ce5;
  color: white;
}

.tenant-management-container .nav-item i {
  margin-right: 10px;
  width: 20px;
  text-align: center;
}

/* 主内容区 */
.tenant-management-container .main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.tenant-management-container .user-avatar {
  margin-left: 15px;
}

/* 内容区域 */
.tenant-management-container .content {
  padding: 30px;
  flex: 1;
}

.tenant-management-container .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tenant-management-container .card-title {
  font-size: 18px;
  font-weight: 500;
}

.tenant-management-container .operation-card,
.tenant-management-container .tenant-list-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}
</style>
