<template>
    <div>
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
                
                <!-- 加速卡和网络详细信息表格 -->
                <el-table :data="aiAccelerators" style="width: 100%; margin-top: 20px" size="small">
                  <el-table-column prop="name" label="加速卡名称" width="180"></el-table-column>
                  <el-table-column prop="type" label="类型" width="120">
                    <template #default="{ row }">
                      <el-tag size="small">{{ row.type }}</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="memoryUsed" label="显存使用" width="150">
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
    </div>
</template>

<script>
export default {
  name: 'HardwareMonitor',
  props: {
    // 节点数据
    nodes: {
      type: Array,
      required: true,
      default: () => []
    },
    // AI加速卡数据
    aiAccelerators: {
      type: Array,
      required: true,
      default: () => []
    },
    // 网络统计数据
    networkStats: {
      type: Object,
      required: true,
      default: () => ({
        totalBandwidth: 0,
        inbound: 0,
        outbound: 0,
        connections: 0,
        interfaces: []
      })
    }
  },
  methods: {
    // 根据使用率获取颜色
    getUsageColor(percent) {
      if (percent < 60) return '#67C23A';
      if (percent < 80) return '#E6A23C';
      return '#F56C6C';
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
}
</script>

<style scoped>

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
