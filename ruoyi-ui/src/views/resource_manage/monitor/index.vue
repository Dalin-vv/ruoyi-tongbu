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

    <Overview :summary="summary"> </Overview>
    <Hardware 
      :nodes="nodes"
      :aiAccelerators="aiAccelerators"
      :networkStats="networkStats"
    ></Hardware>
    <Resource
      :nodes="nodes"
      :pods="pods"
      :aiAccelerators="aiAccelerators"
      :networkStats="networkStats"
      :summary="summary"
    ></Resource>
    
  </div>
  

</template>

<script>

import Overview from './detail/overview.vue';
import Hardware from './detail/hardware.vue';
import { getK8sMonitorData } from '@/api/k8s/monitor'
import Resource from './detail/resource.vue';
export default {
  components: {
    Overview,
    Hardware,
    Resource
  },
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
    // getUsageColor(percent) {
    //   if (percent < 60) return '#67C23A';
    //   if (percent < 80) return '#E6A23C';
    //   return '#F56C6C';
    // },
    
    // 获取Pod状态类型
    // getPodStatusType(status) {
    //   switch (status) {
    //     case 'Running': return 'success';
    //     case 'Pending': return 'warning';
    //     case 'Succeeded': return '';
    //     default: return 'danger';
    //   }
    // },
    
    // 格式化内存
    // formatMemory(value) {
    //   if (value > 1024 * 1024) {
    //     return (value / (1024 * 1024)).toFixed(1) + ' TiB';
    //   }
    //   if (value > 1024) {
    //     return (value / 1024).toFixed(1) + ' GiB';
    //   }
    //   return value.toFixed(1) + ' MiB';
    // },
    
    // // 查看Pod详情
    // showPodDetails(pod) {
    //   console.log('查看Pod详情:', pod.name);
    //   this.$message.info(`查看Pod详情: ${pod.name}`);
    // },
    
    // // 查看Pod日志
    // showPodLogs(pod) {
    //   console.log('查看Pod日志:', pod.name);
    //   this.$message.info(`查看Pod日志: ${pod.name}`);
    // },
    
    // // 获取加速卡状态类型
    // getAcceleratorStatusType(status) {
    //   switch (status) {
    //     case 'Online': return 'success';
    //     case 'Offline': return 'danger';
    //     case 'Busy': return 'warning';
    //     case 'Idle': return 'info';
    //     default: return '';
    //   }
    // },
    
    // // 格式化网络速度
    // formatNetworkSpeed(speed) {
    //   if (speed > 1024 * 1024) {
    //     return (speed / (1024 * 1024)).toFixed(1) + ' Gbps';
    //   }
    //   if (speed > 1024) {
    //     return (speed / 1024).toFixed(1) + ' Mbps';
    //   }
    //   return speed.toFixed(1) + ' Kbps';
    // }
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
</style>
