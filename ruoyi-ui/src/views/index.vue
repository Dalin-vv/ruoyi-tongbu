<template>
  <div class="app-container home">
    <!-- 头部区域 -->
    <div class="header-section">
      <div class="logo-title">
        <i class="el-icon-supervise main-icon"></i>
        <h1>国产算力平台</h1>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <!-- 左侧信息 -->
      <div class="left-panel">
        <h2 class="panel-title">平台核心特性</h2>
        <div class="features">
          <div class="feature-item" v-for="(item, index) in features" :key="index">
            <i :class="item.icon" class="feature-icon"></i>
            <span class="feature-text">{{ item.text }}</span>
          </div>
        </div>

        <h2 class="panel-title tech-title">技术栈</h2>
        <div class="tech-stacks">
          <div class="tech-group">
            <h4>后端</h4>
            <div class="tech-tags">
              <span class="tag" v-for="tech in backendTech" :key="tech">{{ tech }}</span>
            </div>
          </div>
          <div class="tech-group">
            <h4>前端</h4>
            <div class="tech-tags">
              <span class="tag" v-for="tech in frontendTech" :key="tech">{{ tech }}</span>
            </div>
          </div>
        </div>

        <!-- 新增图表区域 -->
        <h2 class="panel-title chart-title">算力资源分布</h2>
        <div class="chart-container">
          <div ref="resourceChart" class="chart-item"></div>
        </div>
      </div>

      <!-- 右侧数据卡片 -->
      <div class="right-panel">
        <el-card class="stat-card">
          <div slot="header">算力概览</div>
          <div class="stat-item" v-for="stat in computingStats" :key="stat.label">
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-value">{{ stat.value }}</div>
          </div>
        </el-card>

        <el-card class="chart-card">
          <div slot="header">资源使用趋势</div>
          <div ref="usageChart" class="usage-chart"></div>
        </el-card>

        <el-card class="update-card">
          <div slot="header">
            <span>系统信息</span>
            <span class="version">v{{ version }}</span>
          </div>
          <div class="update-info">
            <p>最新更新: 优化资源调度算法</p>
            <p>节点状态: 全部在线 ({{ nodeCount }})</p>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="footer">
      <p>© {{ new Date().getFullYear() }} 国产算力平台 - 自主可控计算资源管理系统</p>
    </div>
  </div>
</template>

<script setup name="Index">
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';

const version = ref('3.9.0');
const nodeCount = ref('128');
const resourceChart = ref(null);
const usageChart = ref(null);

// 核心特性
const features = ref([
  { icon: "el-icon-shield", text: "自主可控" },
  { icon: "el-icon-rocket", text: "高性能计算" },
  { icon: "el-icon-balance-scale", text: "智能调度" },
  { icon: "el-icon-lock", text: "安全可靠" },
  { icon: "el-icon-expand", text: "弹性扩展" },
  { icon: "el-icon-cog", text: "易用管理" }
]);

// 后端技术
const backendTech = ref(["SpringBoot", "Spring Security", "MyBatis", "JWT", "Druid"]);

// 前端技术
const frontendTech = ref(["Vue", "Element-ui", "Vuex", "Axios", "ECharts"]);

// 算力统计
const computingStats = ref([
  { label: "GPU节点", value: "32台" },
  { label: "CPU节点", value: "96台" },
  { label: "总算力", value: "128 PFLOPS" },
  { label: "资源利用率", value: "72%" }
]);

// 初始化图表
onMounted(() => {
  // 资源分布图表
  const resourceChartIns = echarts.init(resourceChart.value);
  resourceChartIns.setOption({
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0
    },
    series: [
      {
        name: '算力资源分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 35, name: 'AI训练' },
          { value: 25, name: '科学计算' },
          { value: 20, name: '数据分析' },
          { value: 15, name: '工程仿真' },
          { value: 5, name: '其他应用' }
        ]
      }
    ],
    color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
  });

  // 资源使用趋势图表
  const usageChartIns = echarts.init(usageChart.value);
  usageChartIns.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLabel: {
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      name: '利用率(%)',
      max: 100,
      axisLabel: {
        fontSize: 12
      }
    },
    series: [
      {
        name: 'GPU利用率',
        type: 'line',
        data: [65, 72, 85, 78, 68, 45, 52],
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#409EFF'
        },
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: 'CPU利用率',
        type: 'line',
        data: [42, 55, 60, 58, 70, 35, 40],
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#67C23A'
        },
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  });

  // 响应窗口大小变化
  window.addEventListener('resize', () => {
    resourceChartIns.resize();
    usageChartIns.resize();
  });
});

function goTarget(url) {
  window.open(url, '_blank');
}
</script>

<style scoped lang="scss">
.home {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  color: #333;
  background-color: #f5f7fa;

  // 头部区域
  .header-section {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 15px 30px;
    background-color: #1f2d3d;
    color: white;

    .logo-title {
      display: flex;
      align-items: center;

      .main-icon {
        font-size: 28px;
        margin-right: 12px;
        color: #409EFF;
      }

      h1 {
        font-size: 24px;
        margin: 0;
        font-weight: 500;
      }
    }
  }

  // 主要内容区
  .main-content {
    display: flex;
    flex: 1;
    padding: 20px 30px;
    gap: 20px;
    box-sizing: border-box;
    overflow: hidden;

    .left-panel {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 15px;

      .panel-title {
        font-size: 18px;
        color: #1f2d3d;
        margin: 0;
        padding-bottom: 5px;
        border-bottom: 2px solid #409EFF;
        display: inline-block;
      }

      .features {
        display: flex;
        flex-wrap: wrap;
        gap: 15px;
        padding: 5px 0;
      }

      .feature-item {
        display: flex;
        align-items: center;
        background-color: white;
        padding: 8px 15px;
        border-radius: 4px;
        box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        transition: all 0.2s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }

        .feature-icon {
          color: #409EFF;
          margin-right: 8px;
          font-size: 16px;
        }
      }

      .tech-stacks {
        display: flex;
        gap: 20px;
        padding: 5px 0;
      }

      .tech-group {
        flex: 1;

        h4 {
          margin: 0 0 10px 0;
          font-size: 14px;
          color: #666;
        }
      }

      .tech-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
      }

      .tag {
        background-color: #e8f3ff;
        color: #409EFF;
        padding: 3px 8px;
        border-radius: 3px;
        font-size: 12px;
        white-space: nowrap;
      }

      // 图表容器
      .chart-container {
        flex: 1;
        min-height: 0;
        display: flex;
        align-items: center;
        justify-content: center;

        .chart-item {
          width: 100%;
          height: 100%;
          min-height: 220px;
        }
      }
    }

    .right-panel {
      width: 320px;
      display: flex;
      flex-direction: column;
      gap: 15px;

      .el-card {
        border-radius: 6px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        flex-shrink: 0;
      }

      .el-card__header {
        padding: 12px 15px;
        background-color: #f5f7fa;
        border-bottom: 1px solid #eee;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .version {
          font-size: 12px;
          color: #666;
        }
      }

      .el-card__body {
        padding: 15px;
        margin: 0;
      }

      .stat-item {
        display: flex;
        justify-content: space-between;
        padding: 8px 0;
        border-bottom: 1px dashed #eee;

        &:last-child {
          border-bottom: none;
        }

        .stat-label {
          color: #666;
          font-size: 13px;
        }

        .stat-value {
          font-weight: 500;
          color: #333;
        }
      }

      // 右侧图表
      .chart-card {
        flex-grow: 1;
        display: flex;
        flex-direction: column;

        .usage-chart {
          width: 100%;
          flex-grow: 1;
          min-height: 180px;
        }
      }

      .update-info {
        font-size: 13px;
        color: #666;

        p {
          margin: 8px 0;
          display: flex;
          align-items: center;

          &::before {
            content: "•";
            color: #409EFF;
            margin-right: 6px;
          }
        }
      }
    }
  }

  // 底部区域
  .footer {
    padding: 10px 30px;
    background-color: #f0f2f5;
    border-top: 1px solid #eee;

    p {
      margin: 0;
      font-size: 12px;
      color: #999;
      text-align: center;
    }
  }

  // 响应式调整
  @media (max-width: 1024px) {
    .main-content {
      flex-direction: column;
      overflow-y: auto;
      padding-bottom: 30px;

      .left-panel, .right-panel {
        width: 100%;
      }

      .right-panel {
        flex-direction: row;
        flex-wrap: wrap;
        justify-content: space-between;

        .el-card {
          width: 48%;
        }
      }
    }
  }
}
</style>
