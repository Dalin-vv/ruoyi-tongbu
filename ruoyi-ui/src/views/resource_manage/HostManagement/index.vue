<template>
  <div class="page-container">
    <div>
      <!-- <div>
      <el-header>
        <h1>算力主机管理</h1>
      </el-header>
    </div> -->
      
     <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">算力主机管理</span>
        </div>
      </template>
      <!-- 搜索和操作栏 -->
      <el-row :gutter="20" class="search-bar">
        <el-col :span="6">
          <el-input v-model="searchQuery" placeholder="搜索主机名称或IP" clearable>
            <template #append>
              <el-button icon="el-icon-search"></el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :span="12">
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">
            添加主机
          </el-button>
          <el-button icon="el-icon-refresh" @click="fetchHosts">
            刷新
          </el-button>
        </el-col>
        <el-col :span="6" style="text-align: right;">
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable>
            <el-option label="在线" value="active"></el-option>
            <el-option label="离线" value="inactive"></el-option>
            <el-option label="错误" value="error"></el-option>
          </el-select>
        </el-col>
      </el-row>

      <!-- 主机列表表格 -->
      <el-table 
        :data="filteredHosts" 
        stripe 
        class="table-container"
        v-loading="loading"
      >
        <el-table-column prop="name" label="主机名称" width="240" />
        <el-table-column prop="ip" label="节点IP" width="200" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <span :class="`status-${scope.row.status}`">
              {{ statusText[scope.row.status] }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="cpuUsage" label="CPU使用率" width="180">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.cpuUsage"
              :color="scope.row.cpuUsage > 80 ? '#f56c6c' : (scope.row.cpuUsage > 60 ? '#e6a23c' : '#67c23a')"
              :show-text="false"
              v-if="scope.row.status === 'active'"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="gpuUsage" label="AI加速卡使用率" width="180">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.gpuUsage"
              :color="scope.row.gpuUsage > 80 ? '#f56c6c' : (scope.row.gpuUsage > 60 ? '#e6a23c' : '#67c23a')"
              :show-text="false"
              v-if="scope.row.status === 'active' && scope.row.hasGpu"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="memory" label="内存使用" width="180">
          <template #default="scope">
            <span v-if="scope.row.status === 'active'">
              {{ scope.row.memoryUsed }}GB / {{ scope.row.memoryTotal }}GB
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">
              查看
            </el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页控件 -->
      <el-pagination
        style="margin-top: 20px; justify-content: flex-end;"
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="totalHosts"
        layout="total, sizes, prev, pager, next, jumper"
        @update:current-page="currentPage = $event"
        @update:page-size="pageSize = $event"
      ></el-pagination>
    </el-card>
    </div>
      
    

   


  <!-- 主机详情对话框 -->
  <el-dialog 
    :title="dialogTitle" 
    v-model="detailDialogVisible" 
    width="60%"
  >
    <el-descriptions :column="2" border v-if="currentHost">
      <el-descriptions-item label="主机名称">{{ currentHost.name }}</el-descriptions-item>
      <el-descriptions-item label="节点IP">{{ currentHost.ip }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <span :class="`status-${currentHost.status}`">
          {{ statusText[currentHost.status] }}
        </span>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ currentHost.createTime }}</el-descriptions-item>
      <el-descriptions-item label="CPU架构">{{ currentHost.cpuArch }}</el-descriptions-item>
      <el-descriptions-item label="操作系统">{{ currentHost.osVersion }}</el-descriptions-item>
    </el-descriptions>

    <h3 style="margin-top: 20px;">运行情况</h3>
    
    <el-row :gutter="20" style="margin-top: 15px;">
      <el-col :span="12">
        <el-card class="metrics-card">
          <template #header>
            <span>CPU使用情况</span>
          </template>
          <div v-if="currentHost.status === 'active'">
            <div class="metrics-value">{{ currentHost.cpuUsage }}%</div>
            <div class="progress-container">
              <el-progress 
                :percentage="currentHost.cpuUsage" 
                :color="currentHost.cpuUsage > 80 ? '#f56c6c' : (currentHost.cpuUsage > 60 ? '#e6a23c' : '#67c23a')"
              ></el-progress>
            </div>
          </div>
          <div v-else>主机离线，无法获取数据</div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="metrics-card">
          <template #header>
            <span>AI加速卡使用情况</span>
          </template>
          <div v-if="currentHost.status === 'active' && currentHost.hasGpu">
            <div class="metrics-value">{{ currentHost.gpuUsage }}%</div>
            <div class="progress-container">
              <el-progress 
                :percentage="currentHost.gpuUsage" 
                :color="currentHost.gpuUsage > 80 ? '#f56c6c' : (currentHost.gpuUsage > 60 ? '#e6a23c' : '#67c23a')"
              ></el-progress>
            </div>
          </div>
          <div v-else-if="!currentHost.hasGpu">该主机无AI加速卡</div>
          <div v-else>主机离线，无法获取数据</div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 15px;">
      <el-col :span="12">
        <el-card class="metrics-card">
          <template #header>
            <span>内存使用情况</span>
          </template>
          <div v-if="currentHost.status === 'active'">
            <div class="metrics-value">{{ currentHost.memoryUsed }}GB / {{ currentHost.memoryTotal }}GB</div>
            <div class="progress-container">
              <el-progress 
                :percentage="Math.round((currentHost.memoryUsed / currentHost.memoryTotal) * 100)" 
                :color="(currentHost.memoryUsed / currentHost.memoryTotal) > 0.8 ? '#f56c6c' : ((currentHost.memoryUsed / currentHost.memoryTotal) > 0.6 ? '#e6a23c' : '#67c23a')"
              ></el-progress>
            </div>
          </div>
          <div v-else>主机离线，无法获取数据</div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="metrics-card">
          <template #header>
            <span>存储使用情况</span>
          </template>
          <div v-if="currentHost.status === 'active'">
            <div class="metrics-value">{{ currentHost.diskUsed }}GB / {{ currentHost.diskTotal }}GB</div>
            <div class="progress-container">
              <el-progress 
                :percentage="Math.round((currentHost.diskUsed / currentHost.diskTotal) * 100)" 
                :color="(currentHost.diskUsed / currentHost.diskTotal) > 0.8 ? '#f56c6c' : ((currentHost.diskUsed / currentHost.diskTotal) > 0.6 ? '#e6a23c' : '#67c23a')"
              ></el-progress>
            </div>
          </div>
          <div v-else>主机离线，无法获取数据</div>
        </el-card>
      </el-col>
    </el-row>

    <template #footer>
      <el-button @click="detailDialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleEdit(currentHost)">编辑</el-button>
    </template>
  </el-dialog>

  <!-- 添加/编辑主机对话框 -->
  <el-dialog 
    :title="isEditing ? '编辑主机' : '添加主机'" 
    v-model="editDialogVisible"
    width="500px"
  >
    <el-form :model="editForm" label-width="100px">
      <el-form-item label="主机名称">
        <el-input v-model="editForm.name"></el-input>
      </el-form-item>
      <el-form-item label="节点IP">
        <el-input v-model="editForm.ip"></el-input>
      </el-form-item>
      <el-form-item label="CPU架构">
        <el-select v-model="editForm.cpuArch" placeholder="请选择">
          <el-option label="x86_64" value="x86_64"></el-option>
          <el-option label="ARM64" value="ARM64"></el-option>
          <el-option label="LoongArch" value="LoongArch"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="操作系统">
        <el-input v-model="editForm.osVersion"></el-input>
      </el-form-item>
      <el-form-item label="是否有AI加速卡">
        <el-switch v-model="editForm.hasGpu"></el-switch>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveHost">保存</el-button>
    </template>
  </el-dialog>
</div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listHosts, addHost, updateHost, delHost } from '@/api/resource_manage/host';

export default {
  setup() {
    // 状态文本映射
    const statusText = {
      active: '在线',
      inactive: '离线',
      error: '错误'
    };

    // 搜索和筛选相关
    const searchQuery = ref('');
    const statusFilter = ref('');
    const currentPage = ref(1);
    const pageSize = ref(10);
    const loading = ref(true);
    
    // 主机数据
    const hosts = ref([]);
    const totalHosts = computed(() => hosts.value.length);
    
    // 对话框控制
    const detailDialogVisible = ref(false);
    const editDialogVisible = ref(false);
    const isEditing = ref(false);
    
    // 当前选中的主机
    const currentHost = ref(null);
    const dialogTitle = computed(() => 
      currentHost.value ? `${currentHost.value.name} 的详细信息` : '主机详情'
    );
    
    // 编辑表单
    const editForm = ref({
      id: null,
      name: '',
      ip: '',
      cpuArch: 'x86_64',
      osVersion: 'CentOS 7.9',
      hasGpu: true
    });

    // 过滤后的主机列表
    const filteredHosts = computed(() => {
      let result = hosts.value;
      
      // 应用搜索过滤
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(host => 
          host.name.toLowerCase().includes(query) || 
          host.ip.toLowerCase().includes(query)
        );
      }
      
      // 应用状态过滤
      if (statusFilter.value) {
        result = result.filter(host => host.status === statusFilter.value);
      }
      
      // 应用分页
      const start = (currentPage.value - 1) * pageSize.value;
      const end = start + pageSize.value;
      return result.slice(start, end);
    });

    // 获取主机数据
    const fetchHosts = () => {
      loading.value = true;
      listHosts()
        .then(response => {
          hosts.value = response.data;
          loading.value = false;
        })
        .catch(error => {
          console.error('获取主机列表失败:', error);
          loading.value = false;
          ElMessage.error('获取主机列表失败');
        });
    };

    // 查看主机详情
    const handleView = (host) => {
      currentHost.value = host;
      detailDialogVisible.value = true;
    };

    // 添加主机
    const handleAdd = () => {
      isEditing.value = false;
      editForm.value = {
        id: null,
        name: '',
        ip: '',
        cpuArch: 'x86_64',
        osVersion: 'CentOS 7.9',
        hasGpu: true
      };
      editDialogVisible.value = true;
    };

    // 编辑主机
    const handleEdit = (host) => {
      isEditing.value = true;
      currentHost.value = host;
      // 填充表单数据
      editForm.value = { ...host };
      detailDialogVisible.value = false;
      editDialogVisible.value = true;
    };

    // 删除主机
    const handleDelete = (host) => {
      ElMessageBox.confirm(
        `确定要删除主机 "${host.name}" (${host.ip}) 吗？此操作不可恢复。`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(() => {
        delHost(host.id)
          .then(() => {
            ElMessage.success('删除成功');
            fetchHosts(); // 重新获取数据以刷新列表
          })
          .catch(error => {
            console.error('删除主机失败:', error);
            ElMessage.error('删除主机失败');
          });
      }).catch(() => {
        // 取消删除
      });
    };

    // 保存主机信息
    const saveHost = () => {
      if (isEditing.value) {
        // 更新现有主机
        updateHost(editForm.value)
          .then(() => {
            ElMessage.success('更新成功');
            editDialogVisible.value = false;
            fetchHosts(); // 重新获取数据以刷新列表
          })
          .catch(error => {
            console.error('更新主机失败:', error);
            ElMessage.error('更新主机失败');
          });
      } else {
        // 添加新主机
        addHost(editForm.value)
          .then(() => {
            ElMessage.success('添加成功');
            editDialogVisible.value = false;
            fetchHosts(); // 重新获取数据以刷新列表
          })
          .catch(error => {
            console.error('添加主机失败:', error);
            ElMessage.error('添加主机失败');
          });
      }
    };

    // 组件挂载时获取数据
    onMounted(() => {
      fetchHosts();
    });

    return {
      searchQuery,
      statusFilter,
      currentPage,
      pageSize,
      loading,
      hosts,
      totalHosts,
      filteredHosts,
      statusText,
      detailDialogVisible,
      editDialogVisible,
      isEditing,
      currentHost,
      dialogTitle,
      editForm,
      fetchHosts,
      handleView,
      handleAdd,
      handleEdit,
      handleDelete,
      saveHost
    };
  }
};
</script>

<style scoped>
.page-container {
  display: column;
  padding: 25px;
}
.page-container .card-title {
  font-size: 18px;
  font-weight: 500;
}


.monitor-header {
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.table-container {
  margin-top: 20px;
}
.search-bar {
  margin-bottom: 20px;
}
.status-active {
  color: #67C23A;
}
.status-inactive {
  color: #909399;
}
.status-error {
  color: #F56C6C;
}
.metrics-card {
  margin-bottom: 15px;
}
.metrics-value {
  font-size: 16px;
  font-weight: bold;
}
.progress-container {
  margin-top: 8px;
}
</style>
