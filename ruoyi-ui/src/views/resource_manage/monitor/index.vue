<template>
  <div class="k8s-monitor-container">
    <!-- 顶部标题和控制栏 -->
    <div class="monitor-header">
      <div class="header-left">
        <h1><i class="el-icon-monitor"></i> Kubernetes集群监控</h1>
        <el-tag type="success" size="medium">生产集群</el-tag>
        <el-tag size="medium">K8s版本: v1.26.3</el-tag>
      </div>
      <div class="header-controls">
        <el-button type="primary" @click="refreshData" :loading="loading">
          <i class="el-icon-refresh"></i> 刷新数据
        </el-button>
        <el-switch
          v-model="autoRefresh"
          active-text="自动刷新"
          inactive-text="停止刷新"
          style="margin-left: 15px"
        ></el-switch>
        <el-select v-model="refreshInterval" style="width: 120px; margin-left: 10px">
          <el-option label="30秒" :value="30"></el-option>
          <el-option label="1分钟" :value="60"></el-option>
          <el-option label="5分钟" :value="300"></el-option>
        </el-select>
      </div>
    </div>

    <!-- 集群概览卡片 -->
    <div class="overview-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="metric-card">
            <div class="metric-content">
              <div class="metric-icon" style="background-color: #e6f7ff">
                <i class="el-icon-cpu" style="color: #1890ff"></i>
              </div>
              <div class="metric-info">
                <div class="metric-value">{{ summary.nodes }}</div>
                <div class="metric-label">节点总数</div>
              </div>
            </div>
            <div class="metric-trend">
              <span class="trend-up"><i class="el-icon-top"></i> 2 台运行中</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="metric-card">
            <div class="metric-content">
              <div class="metric-icon" style="background-color: #f6ffed">
                <i class="el-icon-box" style="color: #52c41a"></i>
              </div>
              <div class="metric-info">
                <div class="metric-value">{{ summary.pods }}</div>
                <div class="metric-label">运行中Pod</div>
              </div>
            </div>
            <div class="metric-trend">
              <span class="trend-down"><i class="el-icon-bottom"></i> 3 个异常</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="metric-card">
            <div class="metric-content">
              <div class="metric-icon" style="background-color: #fff7e6">
                <i class="el-icon-s-operation" style="color: #fa8c16"></i>
              </div>
              <div class="metric-info">
                <div class="metric-value">{{ summary.deployments }}</div>
                <div class="metric-label">部署数量</div>
              </div>
            </div>
            <div class="metric-trend">
              <span class="trend-up"><i class="el-icon-top"></i> 1 个新增</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="metric-card">
            <div class="metric-content">
              <div class="metric-icon" style="background-color: #fff0f6">
                <i class="el-icon-warning" style="color: #f5222d"></i>
              </div>
              <div class="metric-info">
                <div class="metric-value">{{ summary.errors }}</div>
                <div class="metric-label">异常状态</div>
              </div>
            </div>
            <div class="metric-trend">
              <span class="trend-warning"><i class="el-icon-warning"></i> 需要关注</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 资源使用图表 -->
    <div class="resource-charts">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="never" class="chart-card">
            <template #header>
              <div class="chart-title">集群CPU使用情况</div>
            </template>
            <div class="chart-container">
              <div class="chart-legend">
                <div><span class="legend-dot" style="background-color: #5470c6"></span> 已使用</div>
                <div><span class="legend-dot" style="background-color: #91cc75"></span> 总量</div>
              </div>
              <div class="chart-wrapper">
                <div class="bar-chart">
                  <div v-for="(node, index) in nodes" :key="index" class="bar-group">
                    <div class="node-name">{{ node.name }}</div>
                    <div class="bar-container">
                      <div 
                        class="bar-used" 
                        :style="{ width: `${node.cpuPercent}%`, background: getUsageColor(node.cpuPercent) }"
                      ></div>
                      <div class="bar-total"></div>
                    </div>
                    <div class="bar-value">{{ node.cpuUsed.toFixed(1) }}/{{ node.cpuTotal }} 核心</div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never" class="chart-card">
            <template #header>
              <div class="chart-title">集群内存使用情况</div>
            </template>
            <div class="chart-container">
              <div class="chart-legend">
                <div><span class="legend-dot" style="background-color: #5470c6"></span> 已使用</div>
                <div><span class="legend-dot" style="background-color: #91cc75"></span> 总量</div>
              </div>
              <div class="chart-wrapper">
                <div class="bar-chart">
                  <div v-for="(node, index) in nodes" :key="index" class="bar-group">
                    <div class="node-name">{{ node.name }}</div>
                    <div class="bar-container">
                      <div 
                        class="bar-used" 
                        :style="{ width: `${node.memPercent}%`, background: getUsageColor(node.memPercent) }"
                      ></div>
                      <div class="bar-total"></div>
                    </div>
                    <div class="bar-value">{{ formatMemory(node.memUsed) }}/{{ formatMemory(node.memTotal) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 节点和Pod状态表格 -->
    <div class="status-tables">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="never" class="table-card">
            <template #header>
              <div class="table-header">
                <span>节点状态</span>
                <el-button type="text" icon="el-icon-s-grid">查看全部</el-button>
              </div>
            </template>
            <el-table :data="nodes" style="width: 100%" height="300">
              <el-table-column prop="name" label="节点名称" width="180">
                <template #default="{ row }">
                  <div class="node-info">
                    <i class="el-icon-cpu"></i>
                    <span>{{ row.name }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'Ready' ? 'success' : 'danger'" size="small">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="CPU使用率">
                <template #default="{ row }">
                  <el-progress 
                    :percentage="row.cpuPercent" 
                    :color="getUsageColor(row.cpuPercent)"
                    :show-text="false"
                    style="width: 80%"
                  ></el-progress>
                  <span style="margin-left: 10px">{{ row.cpuPercent }}%</span>
                </template>
              </el-table-column>
              <el-table-column label="内存使用率">
                <template #default="{ row }">
                  <el-progress 
                    :percentage="row.memPercent" 
                    :color="getUsageColor(row.memPercent)"
                    :show-text="false"
                    style="width: 80%"
                  ></el-progress>
                  <span style="margin-left: 10px">{{ row.memPercent }}%</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never" class="table-card">
            <template #header>
              <div class="table-header">
                <span>Pod状态</span>
                <div>
                  <el-input 
                    v-model="podFilter" 
                    placeholder="搜索Pod名称" 
                    size="small" 
                    style="width: 200px; margin-right: 10px"
                    clearable
                  >
                    <template #prefix>
                      <i class="el-icon-search"></i>
                    </template>
                  </el-input>
                  <el-button type="text" icon="el-icon-s-grid">查看全部</el-button>
                </div>
              </div>
            </template>
            <el-table :data="filteredPods" style="width: 100%" height="300">
              <el-table-column prop="name" label="Pod名称">
                <template #default="{ row }">
                  <div class="pod-info">
                    <i class="el-icon-box"></i>
                    <span>{{ row.name }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="namespace" label="命名空间" width="100"></el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getPodStatusType(row.status)" size="small">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="重启次数" width="80" prop="restarts"></el-table-column>
              <el-table-column label="运行时间" width="100" prop="age"></el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="text" size="small" @click="showPodDetails(row)">详情</el-button>
                  <el-button type="text" size="small" @click="showPodLogs(row)">日志</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getK8sMonitorData } from '@/api/k8s/monitor'

export default {
  name: 'K8sMonitor',
  data() {
    return {
      // 集群概览数据
      summary: {
        nodes: 0,
        pods: 0,
        deployments: 0,
        errors: 0
      },
      
      // 节点数据
      nodes: [],
      
      // Pod数据
      pods: [],
      
      // 过滤条件
      podFilter: '',
      loading: false,
      autoRefresh: false,
      refreshInterval: 60
    };
  },
  computed: {
    // 计算属性 - 过滤后的Pod列表
    filteredPods() {
      if (!this.podFilter) return this.pods;
      return this.pods.filter(pod => 
        pod.name.toLowerCase().includes(this.podFilter.toLowerCase())
      );
    }
  },
  mounted() {
    this.loadData();
  },
  methods: {
    // 加载数据
    async loadData() {
      this.loading = true;
      try {
        const response = await getK8sMonitorData();
        const data = response.data;
        
        // 更新概览数据
        this.summary = data.summary || {
          nodes: 0,
          pods: 0,
          deployments: 0,
          errors: 0
        };
        
        // 更新节点数据
        this.nodes = data.nodes || [];
        
        // 更新Pod数据
        this.pods = data.pods || [];
        
      } catch (error) {
        console.error('获取监控数据失败:', error);
        this.$message.error('获取监控数据失败');
      } finally {
        this.loading = false;
      }
    },
    
    // 刷新数据
    refreshData() {
      this.loadData();
    },
    
    // 根据使用率获取颜色
    getUsageColor(percent) {
      if (percent < 60) return '#67C23A';
      if (percent < 80) return '#E6A23C';
      return '#F56C6C';
    },
    
    // 获取Pod状态类型
    getPodStatusType(status) {
      switch (status) {
        case 'Running': return 'success';
        case 'Pending': return 'warning';
        case 'Succeeded': return '';
        default: return 'danger';
      }
    },
    
    // 格式化内存
    formatMemory(value) {
      if (value > 1024 * 1024) {
        return (value / (1024 * 1024)).toFixed(1) + ' TiB';
      }
      if (value > 1024) {
        return (value / 1024).toFixed(1) + ' GiB';
      }
      return value.toFixed(1) + ' MiB';
    },
    
    // 查看Pod详情
    showPodDetails(pod) {
      console.log('查看Pod详情:', pod.name);
      this.$message.info(`查看Pod详情: ${pod.name}`);
    },
    
    // 查看Pod日志
    showPodLogs(pod) {
      console.log('查看Pod日志:', pod.name);
      this.$message.info(`查看Pod日志: ${pod.name}`);
    }
  }
};
</script>

<style scoped>
.k8s-monitor-container {
  padding: 20px;
}

.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-left h1 {
  margin: 0;
  margin-right: 15px;
  font-size: 20px;
  color: #303133;
}

.header-left .el-tag {
  margin-right: 10px;
}

.header-controls {
  display: flex;
  align-items: center;
}

.overview-cards {
  margin-bottom: 20px;
}

.metric-card {
  border-radius: 8px;
  border: none;
}

.metric-content {
  display: flex;
  align-items: center;
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 15px;
}

.metric-info {
  flex: 1;
}

.metric-value {
  font-size: 28px;
  font-weight: bold;
  line-height: 1;
}

.metric-label {
  font-size: 14px;
  color: #606266;
  margin-top: 5px;
}

.metric-trend {
  margin-top: 10px;
  font-size: 12px;
  text-align: right;
}

.trend-up {
  color: #67c23a;
}

.trend-down {
  color: #f56c6c;
}

.trend-warning {
  color: #e6a23c;
}

.resource-charts {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 8px;
}

.chart-title {
  font-weight: bold;
  color: #303133;
}

.chart-container {
  padding: 10px;
}

.chart-legend {
  display: flex;
  margin-bottom: 15px;
}

.chart-legend div {
  display: flex;
  align-items: center;
  margin-right: 20px;
  font-size: 13px;
  color: #606266;
}

.legend-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 5px;
}

.chart-wrapper {
  height: 200px;
}

.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.bar-group {
  display: flex;
  align-items: center;
}

.node-name {
  width: 150px;
  font-size: 13px;
  color: #606266;
}

.bar-container {
  flex: 1;
  height: 20px;
  background: #ebeef5;
  border-radius: 10px;
  position: relative;
  overflow: hidden;
}

.bar-used {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  border-radius: 10px 0 0 10px;
}

.bar-total {
  height: 100%;
}

.bar-value {
  width: 180px;
  text-align: right;
  font-size: 13px;
  color: #606266;
  padding-left: 10px;
}

.status-tables {
  margin-bottom: 20px;
}

.table-card {
  border-radius: 8px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #303133;
}

.node-info, .pod-info {
  display: flex;
  align-items: center;
}

.node-info i, .pod-info i {
  margin-right: 8px;
  font-size: 16px;
}

.node-info i {
  color: #1890ff;
}

.pod-info i {
  color: #52c41a;
}
</style>
