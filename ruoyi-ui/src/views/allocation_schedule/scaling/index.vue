<template>
  <div class="app-container">

    <!-- 主内容区域 -->
    <main class="main-content">
      <!-- 统计信息 -->
      <div class="stats">
        <div class="stat-card">
          <div class="stat-value">{{ stats.currentContainers }}</div>
          <div class="stat-label">当前容器数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.maxContainers }}</div>
          <div class="stat-label">最大容器数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.minContainers }}</div>
          <div class="stat-label">最小容器数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.scaleEvents }}</div>
          <div class="stat-label">今日伸缩事件</div>
        </div>
      </div>

      <!-- 页面标题 -->
      <div class="page-title">
        <el-button 
          :type="autoScalingEnabled ? 'success' : 'danger'" 
          @click="toggleAutoScaling"
          
        >
          {{ autoScalingEnabled ? '自动伸缩已启用' : '自动伸缩已禁用' }}
        </el-button>
      </div>
      
      <!-- 监控区域 -->
      <div class="monitor-section">
        <div class="chart-container">
          <div class="chart-title">资源使用率监控</div>
          <div class="usage-chart">
            <!-- 图表占位，实际项目中可接入ECharts等图表库 -->
            <div style="height: 100%; display: flex; align-items: center; justify-content: center; background: #fafafa; border-radius: 4px;">
              <div style="text-align: center;">
                <p>CPU/内存/GPU使用率实时图表</p>
                <p>(实际项目中将显示实时折线图)</p>
              </div>
            </div>
          </div>
          <div class="resource-usage">
            <div class="usage-item">
              <div class="usage-value">{{ cpuUsage.toFixed(1) }}%</div>
              <div class="usage-label">CPU使用率</div>
            </div>
            <div class="usage-item">
              <div class="usage-value">{{ memoryUsage.toFixed(1) }}%</div>
              <div class="usage-label">内存使用率</div>
            </div>
            <div class="usage-item">
              <div class="usage-value">{{ gpuUsage.toFixed(1) }}%</div>
              <div class="usage-label">GPU使用率</div>
            </div>
          </div>
        </div>
        
        <div class="containers-status">
          <div class="chart-title">容器状态</div>
          <div class="status-list">
            <div class="status-item" v-for="(container, index) in containers" :key="index">
              <span>{{ container.name }}</span>
              <el-tag :type="container.status === '运行中' ? 'success' : 'danger'" size="small">{{ container.status }}</el-tag>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 策略配置 -->
      <div class="policy-config">
        <div class="chart-title">伸缩策略配置</div>
        <el-form class="policy-form" :model="scalingPolicy" label-width="100px">
          <div class="policy-row">
            <div class="slider-item">
              <span class="slider-label">CPU阈值扩容: {{ scalingPolicy.cpuScaleOutThreshold }}%</span>
              <el-slider 
                v-model="scalingPolicy.cpuScaleOutThreshold" 
                :min="50" 
                :max="100"
                show-input
              ></el-slider>
            </div>
            <div class="slider-item">
              <span class="slider-label">CPU阈值缩容: {{ scalingPolicy.cpuScaleInThreshold }}%</span>
              <el-slider 
                v-model="scalingPolicy.cpuScaleInThreshold" 
                :min="0" 
                :max="50"
                show-input
              ></el-slider>
            </div>
          </div>
          
          <div class="policy-row">
            <div class="slider-item">
              <span class="slider-label">内存阈值扩容: {{ scalingPolicy.memoryScaleOutThreshold }}%</span>
              <el-slider 
                v-model="scalingPolicy.memoryScaleOutThreshold" 
                :min="50" 
                :max="100"
                show-input
              ></el-slider>
            </div>
            <div class="slider-item">
              <span class="slider-label">内存阈值缩容: {{ scalingPolicy.memoryScaleInThreshold }}%</span>
              <el-slider 
                v-model="scalingPolicy.memoryScaleInThreshold" 
                :min="0" 
                :max="50"
                show-input
              ></el-slider>
            </div>
          </div>
          
          <div class="policy-row">
            <div class="slider-item">
              <span class="slider-label">GPU阈值扩容: {{ scalingPolicy.gpuScaleOutThreshold }}%</span>
              <el-slider 
                v-model="scalingPolicy.gpuScaleOutThreshold" 
                :min="50" 
                :max="100"
                show-input
              ></el-slider>
            </div>
            <div class="slider-item">
              <span class="slider-label">GPU阈值缩容: {{ scalingPolicy.gpuScaleInThreshold }}%</span>
              <el-slider 
                v-model="scalingPolicy.gpuScaleInThreshold" 
                :min="0" 
                :max="50"
                show-input
              ></el-slider>
            </div>
          </div>
          
          <div class="policy-row">
            <el-form-item label="最大容器数">
              <el-input-number 
                v-model="scalingPolicy.maxContainers" 
                :min="scalingPolicy.minContainers + 1" 
                :max="20"
                controls-position="right"
              ></el-input-number>
            </el-form-item>
            <el-form-item label="最小容器数">
              <el-input-number 
                v-model="scalingPolicy.minContainers" 
                :min="1" 
                :max="scalingPolicy.maxContainers - 1"
                controls-position="right"
              ></el-input-number>
            </el-form-item>
          </div>
          
          <el-form-item>
            <el-button type="primary" @click="saveScalingPolicy">保存策略</el-button>
            <el-button @click="resetScalingPolicy">重置默认</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 伸缩历史 -->
      <div class="history-section">
        <div class="chart-title">伸缩历史记录</div>
        <el-table :data="scaleHistory" stripe>
          <el-table-column prop="time" label="时间" width="150"></el-table-column>
          <el-table-column prop="event" label="事件" width="120">
            <template #default="scope">
              <span :class="scope.row.eventType">{{ scope.row.event }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="原因"></el-table-column>
          <el-table-column prop="oldCount" label="原容器数" width="90"></el-table-column>
          <el-table-column prop="newCount" label="新容器数" width="90"></el-table-column>
        </el-table>
      </div>
    </main>

    <footer class="footer">
      <p>© 2023 动态伸缩管理系统 - 基于资源使用情况的智能伸缩</p>
    </footer>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';

export default {
  name: 'DynamicScalingSystem',
  setup() {
    const stats = reactive({
      currentContainers: 5,
      maxContainers: 10,
      minContainers: 2,
      scaleEvents: 3
    });
    
    const autoScalingEnabled = ref(true);
    const cpuUsage = ref(65);
    const memoryUsage = ref(42);
    const gpuUsage = ref(78);
    
    const containers = ref([
      { name: '模型推理服务-1', status: '运行中' },
      { name: '模型推理服务-2', status: '运行中' },
      { name: '数据处理服务', status: '运行中' },
      { name: '训练任务服务', status: '运行中' },
      { name: '批量预测服务', status: '已停止' }
    ]);
    
    const scalingPolicy = reactive({
      cpuScaleOutThreshold: 80,
      cpuScaleInThreshold: 30,
      memoryScaleOutThreshold: 85,
      memoryScaleInThreshold: 40,
      gpuScaleOutThreshold: 75,
      gpuScaleInThreshold: 25,
      maxContainers: 10,
      minContainers: 2
    });
    
    const scaleHistory = ref([
      { time: '2023-06-20 10:25:36', event: '扩容', eventType: 'scale-up', reason: 'CPU使用率超过阈值(82%)', oldCount: 4, newCount: 5 },
      { time: '2023-06-20 09:15:22', event: '缩容', eventType: 'scale-down', reason: '内存使用率低于阈值(35%)', oldCount: 5, newCount: 4 },
      { time: '2023-06-20 08:05:18', event: '扩容', eventType: 'scale-up', reason: 'GPU使用率超过阈值(78%)', oldCount: 3, newCount: 4 }
    ]);
    
    let intervalId = null;
    
    const toggleAutoScaling = () => {
      autoScalingEnabled.value = !autoScalingEnabled.value;
      ElMessage.success(`自动伸缩已${autoScalingEnabled.value ? '启用' : '禁用'}`);
    };
    
    const saveScalingPolicy = () => {
      ElMessage.success('伸缩策略保存成功');
      // 模拟策略应用后的效果
      stats.maxContainers = scalingPolicy.maxContainers;
      stats.minContainers = scalingPolicy.minContainers;
    };
    
    const resetScalingPolicy = () => {
      Object.assign(scalingPolicy, {
        cpuScaleOutThreshold: 80,
        cpuScaleInThreshold: 30,
        memoryScaleOutThreshold: 85,
        memoryScaleInThreshold: 40,
        gpuScaleOutThreshold: 75,
        gpuScaleInThreshold: 25,
        maxContainers: 10,
        minContainers: 2
      });
      ElMessage.info('策略已重置为默认值');
    };
    
    onMounted(() => {
      // 模拟实时数据更新
      intervalId = setInterval(() => {
        // 随机波动资源使用率
        cpuUsage.value = Math.max(10, Math.min(95, cpuUsage.value + (Math.random() * 10 - 5)));
        memoryUsage.value = Math.max(15, Math.min(90, memoryUsage.value + (Math.random() * 8 - 4)));
        gpuUsage.value = Math.max(20, Math.min(98, gpuUsage.value + (Math.random() * 12 - 6)));
      }, 3000);
    });
    
    onUnmounted(() => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    });
    
    return {
      stats,
      autoScalingEnabled,
      cpuUsage,
      memoryUsage,
      gpuUsage,
      containers,
      scalingPolicy,
      scaleHistory,
      toggleAutoScaling,
      saveScalingPolicy,
      resetScalingPolicy
    };
  }
};
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
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
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
  max-width: 1500px;
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
.monitor-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}
@media (max-width: 992px) {
  .monitor-section {
    grid-template-columns: 1fr;
  }
}
.chart-container {
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}
.chart-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 15px;
  color: #303133;
}
.usage-chart {
  height: 300px;
  margin-bottom: 20px;
}
.resource-usage {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}
.usage-item {
  text-align: center;
  padding: 15px;
  background: linear-gradient(45deg, #fdf6ec, #fef8f0);
  border-radius: 6px;
}
.usage-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 5px;
}
.usage-label {
  font-size: 14px;
  color: #909399;
}
.containers-status {
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}
.status-list {
  margin-top: 15px;
}
.status-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}
.status-item:last-child {
  border-bottom: none;
}
.policy-config {
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}
.policy-form {
  margin-top: 20px;
}
.policy-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}
@media (max-width: 768px) {
  .policy-row {
    grid-template-columns: 1fr;
  }
}
.slider-item {
  margin-bottom: 25px;
}
.slider-label {
  display: block;
  margin-bottom: 8px;
  color: #606266;
}
.history-section {
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}
.footer {
  text-align: center;
  padding: 20px;
  color: #909399;
  font-size: 14px;
  margin-top: auto;
}
.scale-event {
  color: #e6a23c;
}
.scale-up {
  color: #67c23a;
}
.scale-down {
  color: #f56c6c;
}
@media (max-width: 768px) {
  .stats {
    grid-template-columns: 1fr 1fr;
  }
  .resource-usage {
    grid-template-columns: 1fr;
  }
}
</style>
