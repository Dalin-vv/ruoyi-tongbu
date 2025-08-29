<template>
    <div id="app" class="app-container">
  
        <!-- 主内容区域 -->
        <main class="main-content">
            <!-- 统计信息 -->
            <div class="stats">
                <div class="stats-title">系统概览</div>
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-value">{{ stats.totalTemplates }}</div>
                        <div class="stat-label">总模板数</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">{{ stats.activeTemplates }}</div>
                        <div class="stat-label">活跃模板</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">{{ stats.cpuTotal }} 核</div>
                        <div class="stat-label">CPU总量</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-value">{{ stats.gpuTotal }} 卡</div>
                        <div class="stat-label">GPU总量</div>
                    </div>
                </div>
            </div>

            <!-- 页面标题和操作按钮 -->
            <div class="page-title">
                <h2>算力模板管理</h2>
                <el-button type="primary" @click="handleNewTemplate">新建模板</el-button>
            </div>
            
            <!-- 模板列表 -->
            <div class="template-grid">
                <div class="template-card" v-for="(template, index) in computeTemplates" :key="index">
                    <div class="template-header">
                        <div class="template-name">{{ template.name }}</div>
                        <span class="template-type">{{ getTypeName(template.type) }}</span>
                    </div>
                    <div class="template-body">
                        <div class="spec-item">
                            <span class="spec-label">计算类型:</span>
                            <span class="spec-value">{{ getTypeName(template.type) }}</span>
                        </div>
                        <div class="spec-item">
                            <span class="spec-label">CPU:</span>
                            <span class="spec-value">{{ template.cpu }} 核</span>
                        </div>
                        <div class="spec-item">
                            <span class="spec-label">内存:</span>
                            <span class="spec-value">{{ template.memory }} GB</span>
                        </div>
                        <div class="spec-item">
                            <span class="spec-label">GPU:</span>
                            <span class="spec-value">{{ template.gpu }} 卡</span>
                        </div>
                        <div class="spec-item">
                            <span class="spec-label">存储:</span>
                            <span class="spec-value">{{ template.storage }} GB</span>
                        </div>
                    </div>
                    <div class="template-footer">
                        <el-button size="mini" @click="editTemplate(template)">编辑</el-button>
                        <el-button size="mini" type="primary"  @click="applyTemplate(template)">应用</el-button>
                        <el-button size="mini" type="danger" @click="confirmDeleteTemplate(template)">删除</el-button>
                    </div>
                </div>
            </div>
        </main>


        <!-- 算力模板对话框 -->
        <el-dialog :title="isEditTemplate ? '编辑算力模板' : '新建算力模板'" v-model="dialogTemplateVisible" width="600px">
            <el-form :model="currentTemplate" label-width="120px">
                <el-form-item label="模板名称">
                    <el-input v-model="currentTemplate.name" placeholder="请输入模板名称"></el-input>
                </el-form-item>
                <el-form-item label="计算类型">
                    <el-select v-model="currentTemplate.type" placeholder="请选择计算类型">
                        <el-option label="CPU计算" value="cpu"></el-option>
                        <el-option label="GPU计算" value="gpu"></el-option>
                        <el-option label="混合计算" value="hybrid"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="CPU核数">
                    <el-input-number v-model="currentTemplate.cpu" :min="1" :max="128" controls-position="right"></el-input-number>
                </el-form-item>
                <el-form-item label="内存(GB)">
                    <el-input-number v-model="currentTemplate.memory" :min="1" :max="256" controls-position="right"></el-input-number>
                </el-form-item>
                <el-form-item label="GPU卡数" v-if="currentTemplate.type !== 'cpu'">
                    <el-input-number v-model="currentTemplate.gpu" :min="1" :max="8" controls-position="right"></el-input-number>
                </el-form-item>
                <el-form-item label="存储(GB)">
                    <el-input-number v-model="currentTemplate.storage" :min="10" :max="1024" controls-position="right"></el-input-number>
                </el-form-item>
            </el-form>
            <span class="dialog-footer">
                <el-button @click="dialogTemplateVisible = false">取消</el-button>
                <el-button type="primary" @click="saveTemplate">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { getTemplateStats, listTemplates, addTemplate, updateTemplate, deleteTemplate } from '@/api/allocation/template'

export default {
 data() {
                return {
                    stats: {
                        totalTemplates: 0,
                        activeTemplates: 0,
                        cpuTotal: 0,
                        gpuTotal: 0
                    },
                    computeTemplates: [],
                    dialogTemplateVisible: false,
                    isEditTemplate: false,
                    currentTemplate: {
                        name: '',
                        type: '',
                        cpu: 4,
                        memory: 8,
                        gpu: 0,
                        storage: 100
                    }
                }
            },
            created() {
                this.loadTemplateData()
            },
            methods: {
                // 加载模板数据
                loadTemplateData() {
                    Promise.all([
                        getTemplateStats(),
                        listTemplates()
                    ]).then(([statsRes, templatesRes]) => {
                        this.stats = statsRes.data
                        this.computeTemplates = templatesRes.data
                    }).catch(error => {
                        console.error('加载模板数据失败:', error)
                        this.$message.error('加载模板数据失败')
                    })
                },
                getTypeName(type) {
                    const typeMap = {
                        'cpu': 'CPU计算',
                        'gpu': 'GPU计算',
                        'hybrid': '混合计算'
                    };
                    return typeMap[type] || '未知类型';
                },
                applyTemplate(template) {
                    this.$message.success(`已应用模板: ${template.name}`);
                },
                handleNewTemplate() {
                    this.resetCurrentTemplate();
                    this.dialogTemplateVisible = true;
                },
                editTemplate(template) {
                    this.currentTemplate = {...template};
                    this.isEditTemplate = true;
                    this.dialogTemplateVisible = true;
                },
                saveTemplate() {
                    if (this.isEditTemplate) {
                        // 更新模板
                        updateTemplate(this.currentTemplate).then(() => {
                            this.$message.success('模板更新成功');
                            this.loadTemplateData(); // 重新加载数据
                            this.dialogTemplateVisible = false;
                            this.resetCurrentTemplate();
                        }).catch(error => {
                            console.error('更新模板失败:', error)
                            this.$message.error('更新模板失败');
                        });
                    } else {
                        // 添加新模板
                        addTemplate(this.currentTemplate).then(() => {
                            this.$message.success('模板创建成功');
                            this.loadTemplateData(); // 重新加载数据
                            this.dialogTemplateVisible = false;
                            this.resetCurrentTemplate();
                        }).catch(error => {
                            console.error('创建模板失败:', error)
                            this.$message.error('创建模板失败');
                        });
                    }
                },
                resetCurrentTemplate() {
                    this.currentTemplate = {
                        name: '',
                        type: '',
                        cpu: 4,
                        memory: 8,
                        gpu: 0,
                        storage: 100
                    };
                    this.isEditTemplate = false;
                },
                confirmDeleteTemplate(template) {
                    this.$confirm(`确定要删除模板 "${template.name}" 吗？`, '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        this.deleteTemplate(template);
                    }).catch(() => {
                        this.$message.info('已取消删除');
                    });
                },
                deleteTemplate(template) {
                    deleteTemplate(template.id).then(() => {
                        this.$message.success('模板删除成功');
                        this.loadTemplateData(); // 重新加载数据
                    }).catch(error => {
                        console.error('删除模板失败:', error)
                        this.$message.error('删除模板失败');
                    });
                }
            }
}
</script>

<style scoped>
.app-container {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .header {
            background: linear-gradient(135deg, #409EFF 0%, #64b5ff 100%);
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
            max-width: 1400px;
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
        .template-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .template-card {
            transition: all 0.3s;
            border-radius: 6px;
            overflow: hidden;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        }
        .template-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 16px 0 rgba(0, 0, 0, 0.15);
        }
        .template-header {
            background: linear-gradient(90deg, #ecf5ff 0%, #d9ecff 100%);
            padding: 15px;
            border-bottom: 1px solid #ebeef5;
        }
        .template-name {
            font-size: 16px;
            font-weight: 600;
            color: #409EFF;
            margin-bottom: 5px;
        }
        .template-type {
            display: inline-block;
            padding: 4px 8px;
            background-color: #ecf5ff;
            color: #409EFF;
            border-radius: 4px;
            font-size: 12px;
        }
        .template-body {
            padding: 15px;
            background-color: #fff;
        }
        .spec-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            padding-bottom: 10px;
            border-bottom: 1px dashed #ebeef5;
        }
        .spec-item:last-child {
            margin-bottom: 0;
            padding-bottom: 0;
            border-bottom: none;
        }
        .spec-label {
            color: #909399;
        }
        .spec-value {
            font-weight: 500;
            color: #606266;
        }
        .template-footer {
            padding: 15px;
            background-color: #fafafa;
            display: flex;
            justify-content: space-between;
        }
        .stats {
            padding: 20px;
            background-color: #fff;
            border-radius: 6px;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
        .stats-title {
            font-size: 16px;
            font-weight: 500;
            margin-bottom: 15px;
            color: #303133;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 15px;
        }
        .stat-card {
            padding: 15px;
            background: linear-gradient(45deg, #f0f7ff, #f9fbfd);
            border-radius: 6px;
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
        .footer {
            text-align: center;
            padding: 20px;
            color: #909399;
            font-size: 14px;
            margin-top: auto;
        }
        @media (max-width: 768px) {
            .template-grid {
                grid-template-columns: 1fr;
            }
            .stats-grid {
                grid-template-columns: 1fr 1fr;
            }
        }
        
        .dialog-footer {
            display: flex;
            justify-content: center;
            gap: 10px;
        }
</style>
