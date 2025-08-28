<template>
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
</template>

<script>
export default {
  name: 'Overview',
  props: {
    summary: {
      type: Object,
      required: true,
      default: () => ({
        nodes: 0,
        pods: 0,
        deployments: 0,
        errors: 0
      })
    }
  }
}
</script>

<style scoped>
.overview-cards {
  margin-bottom: 20px;
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

</style>
