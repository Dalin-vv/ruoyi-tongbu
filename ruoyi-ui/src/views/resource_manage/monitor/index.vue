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
                    <div class="bar-value">
                      <el-tag type="warning" size="small">{{ node.cpuPercent }}%</el-tag>
                    </div>
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
                    <div class="bar-value">
                      <el-tag type="warning" size="small">{{ node.memPercent }}%</el-tag>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- AI加速卡使用情况 -->
    <div class="resource-charts">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="never" class="chart-card">
            <template #header>
              <div class="chart-title">
                <i class="el-icon-cpu"></i> AI加速卡使用情况
              </div>
            </template>
            <div class="chart-container">
              <div v-if="aiAccelerators.length === 0" class="no-data">
                <i class="el-icon-info"></i>
                <p>暂无AI加速卡数据</p>
              </div>
              <div v-else>
                <div class="chart-legend">
                  <div><span class="legend-dot" style="background-color: #5470c6"></span> 已使用</div>
                  <div><span class="legend-dot" style="background-color: #91cc75"></span> 总量</div>
                </div>
                <div class="chart-wrapper">
                  <div class="bar-chart">
                    <div v-for="(accel, index) in aiAccelerators" :key="index" class="bar-group">
                      <div class="node-name">{{ accel.name }}</div>
                      <div class="bar-container">
                        <div 
                          class="bar-used" 
                          :style="{ width: `${accel.utilization}%`, background: getUsageColor(accel.utilization) }"
                        ></div>
                        <div class="bar-total"></div>
                      </div>
                      <div class="bar-value">{{ accel.utilization }}% 使用率</div>
                      <div class="bar-value">
                        <el-tag :type="getAcceleratorStatusType(accel.status)" size="small">
                          {{ accel.status }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 加速卡详细信息表格 -->
                <el-table :data="aiAccelerators" style="width: 100%; margin-top: 20px" size="small">
                  <el-table-column prop="name" label="加速卡名称" width="150"></el-table-column>
                  <el-table-column prop="type" label="类型" width="120">
                    <template #default="{ row }">
                      <el-tag size="small">{{ row.type }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="memoryUsed" label="显存使用" width="120">
                    <template #default="{ row }">
                      {{ formatMemory(row.memoryUsed) }}/{{ formatMemory(row.memoryTotal) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="temperature" label="温度" width="100">
                    <template #default="{ row }">
                      <span :style="{ color: row.temperature > 80 ? '#f56c6c' : '#67c23a' }">
                        {{ row.temperature }}°C
                      </span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="powerUsage" label="功耗" width="100">
                    <template #default="{ row }">
                      {{ row.powerUsage }}W
                    </template>
                  </el-table-column>
                  <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag :type="getAcceleratorStatusType(row.status)" size="small">
                        {{ row.status }}
                      </el-tag>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never" class="chart-card">
            <template #header>
              <div class="chart-title">
                <i class="el-icon-connection"></i> 网络使用情况
              </div>
            </template>
            <div class="chart-container">
              <div v-if="networkStats.interfaces.length === 0" class="no-data">
                <i class="el-icon-info"></i>
                <p>暂无网络数据</p>
              </div>
              <div v-else>
                <div class="network-stats">
                  <div class="network-metric">
                    <div class="metric-label">总带宽使用</div>
                    <div class="metric-value">{{ networkStats.totalBandwidth }} Mbps</div>
                  </div>
                  <div class="network-metric">
                    <div class="metric-label">入站流量</div>
                    <div class="metric-value">{{ formatNetworkSpeed(networkStats.inbound) }}</div>
                  </div>
                  <div class="network-metric">
                    <div class="metric-label">出站流量</div>
                    <div class="metric-value">{{ formatNetworkSpeed(networkStats.outbound) }}</div>
                  </div>
                  <div class="network-metric">
                    <div class="metric-label">连接数</div>
                    <div class="metric-value">{{ networkStats.connections }}</div>
                  </div>
                </div>
                
                <!-- 网络接口详情表格 -->
                <el-table :data="networkStats.interfaces" style="width: 100%; margin-top: 20px" size="small">
                  <el-table-column prop="name" label="接口名称" width="120"></el-table-column>
                  <el-table-column prop="ip" label="IP地址" width="150"></el-table-column>
                  <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag :type="row.status === 'up' ? 'success' : 'danger'" size="small">
                        {{ row.status === 'up' ? '在线' : '离线' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="接收速率" width="120">
                    <template #default="{ row }">
                      {{ formatNetworkSpeed(row.rxRate) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="发送速率" width="120">
                    <template #default="{ row }">
                      {{ formatNetworkSpeed(row.txRate) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="错误率" width="100">
                    <template #default="{ row }">
                      <span :style="{ color: row.errorRate > 0.1 ? '#f56c6c' : '#67c23a' }">
                        {{ (row.errorRate * 100).toFixed(2) }}%
                      </span>
                    </template>
                  </el-table-column>
                </el-table>
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
              <el-table-column prop="name" label="节点名称" width="200">
                <template #default="{ row }">
                  <div class="node-info">
                    <i class="el-icon-cpu"></i>
                    <span>{{ row.name }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'Ready' ? 'success' : 'danger'" size="small">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="os" width="150">
                <template #default="{ row }">
                  <el-tag :type="primary" size="small">
                    {{ row.os }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="ip地址" width="150">
                <template #default="{ row }">
                  <el-tag type="info" size="small">
                    {{ row.ip }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="kubelet" width="150">
                <template #default="{ row }">
                  <el-tag type="primary" size="small">
                    {{ row.kubelet }}
                  </el-tag>
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
              <el-table-column label="操作" width="140">
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
      
      // AI加速卡数据
      aiAccelerators: [],
      
      // 网络统计数据
      networkStats: {
        totalBandwidth: 0,
        inbound: 0,
        outbound: 0,
        connections: 0,
        interfaces: []
      },
      
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
        
        // 更新AI加速卡数据
        this.aiAccelerators = data.aiAccelerators || [];
        
        // 更新网络统计数据
        this.networkStats = data.networkStats || {
          totalBandwidth: 0,
          inbound: 0,
          outbound: 0,
          connections: 0,
          interfaces: []
        };
        
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
    },
    
    // 获取加速卡状态类型
    getAcceleratorStatusType(status) {
      switch (status) {
        case 'Online': return 'success';
        case 'Offline': return 'danger';
        case 'Busy': return 'warning';
        case 'Idle': return 'info';
        default: return '';
      }
    },
    
    // 格式化网络速度
    formatNetworkSpeed(speed) {
      if (speed > 1024 * 1024) {
        return (speed / (1024 * 1024)).toFixed(1) + ' Gbps';
      }
      if (speed > 1024) {
        return (speed / 1024).toFixed(1) + ' Mbps';
      }
      return speed.toFixed(1) + ' Kbps';
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
  width: 120px;
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

/* 网络统计样式 */
.network-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.network-metric {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  text-align: center;
}

.metric-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 5px;
}

.metric-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

/* 无数据样式 */
.no-data {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.no-data i {
  font-size: 48px;
  margin-bottom: 10px;
  display: block;
}
</style>
