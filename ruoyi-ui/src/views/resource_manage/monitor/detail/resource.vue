<template>
  <div class="status-tables">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="never" class="table-card">
            <template #header>
              <div class="table-header" style="display: flex; justify-content: space-between; align-items: center;">
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
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="getNodeStatusType(row.status)" size="small">
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
              <div class="table-header" style="display: flex; justify-content: space-between; align-items: center;">
                <span>Pod状态</span>
                <div style="display: flex; align-items: center;">
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
</template>

<script>
export default {
  name: 'ResourceMonitor',
  
  props: {
    // 从父组件传递的节点数据
    nodes: {
      type: Array,
      default: () => []
    },
    // 从父组件传递的Pod数据
    pods: {
      type: Array,
      default: () => []
    },
    // 从父组件传递的AI加速卡数据
    aiAccelerators: {
      type: Array,
      default: () => []
    },
    // 从父组件传递的网络统计数据
    networkStats: {
      type: Object,
      default: () => ({})
    },
    // 从父组件传递的集群概览数据
    summary: {
      type: Object,
      default: () => ({})
    }
  },
  
  data() {
    return {
      // Pod搜索过滤条件
      podFilter: ''
    };
  },
  
  computed: {
    // 计算属性 - 过滤后的Pod列表
    filteredPods() {
      if (!this.podFilter) return this.pods;
      return this.pods.filter(pod => 
        pod.name && pod.name.toLowerCase().includes(this.podFilter.toLowerCase())
      );
    }
  },
  
  methods: {
    // 查看Pod详情
    showPodDetails(pod) {
      console.log('查看Pod详情:', pod);
      this.$message.info(`查看Pod详情: ${pod.name}`);
      // 这里可以触发显示Pod详情的弹窗或跳转到详情页面
    },
    
    // 查看Pod日志
    showPodLogs(pod) {
      console.log('查看Pod日志:', pod);
      this.$message.info(`查看Pod日志: ${pod.name}`);
      // 这里可以触发显示Pod日志的弹窗
    },
    
    // 获取节点状态类型
    getNodeStatusType(status) {
      switch (status) {
        case 'Ready': return 'success';
        case 'NotReady': return 'danger';
        case 'Unknown': return 'warning';
        default: return 'info';
      }
    },
    
    // 获取Pod状态类型
    getPodStatusType(status) {
      switch (status) {
        case 'Running': return 'success';
        case 'Pending': return 'warning';
        case 'Succeeded': return '';
        case 'Failed': return 'danger';
        case 'Unknown': return 'info';
        default: return '';
      }
    }
  },
  
  watch: {
    // 监听父组件传递的数据变化
    nodes: {
      handler(newNodes) {
        console.log('节点数据更新:', newNodes);
      },
      deep: true
    },
    
    pods: {
      handler(newPods) {
        console.log('Pod数据更新:', newPods);
      },
      deep: true
    }
  },
  
  mounted() {
    console.log('ResourceMonitor组件已挂载，接收到的数据:', {
      nodes: this.nodes,
      pods: this.pods,
      aiAccelerators: this.aiAccelerators,
      networkStats: this.networkStats,
      summary: this.summary
    });
  }
}
</script>

<style>

</style>
