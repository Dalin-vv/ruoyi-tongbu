import request from '@/utils/request'

// 获取分配统计信息
export function getAllocationStats() {
  return request({
    url: '/allocation/stats',
    method: 'get'
  })
}

// 获取资源编配列表
export function listResources() {
  return request({
    url: '/allocation/resources',
    method: 'get'
  })
}

// 获取工作负载编配列表
export function listWorkloads() {
  return request({
    url: '/allocation/workloads',
    method: 'get'
  })
}

// 获取AI模型列表
export function listAIModels() {
  return request({
    url: '/allocation/ai-models',
    method: 'get'
  })
}

// 获取AI加速卡列表
export function listAIAccelerators() {
  return request({
    url: '/allocation/ai-accelerators',
    method: 'get'
  })
}

// 获取计算模板列表
export function listComputeTemplates() {
  return request({
    url: '/allocation/compute-templates',
    method: 'get'
  })
}
// 添加资源限制
export function addResource(data) {
  return request({
    url: '/allocation/resources',
    method: 'post',
    data: data
  })
}

// 更新资源限制
export function updateResource(data) {
  return request({
    url: '/allocation/resources',
    method: 'put',
    data: data
  })
}

// 切换资源状态
export function toggleResourceStatus(resourceName) {
  return request({
    url: '/allocation/resources/' + resourceName + '/toggle-status',
    method: 'put'
  })
}

// 添加工作负载
export function addWorkload(data) {
  return request({
    url: '/allocation/workloads',
    method: 'post',
    data: data
  })
}

// 管理工作负载状态
export function manageWorkloadStatus(workloadId, action) {
  return request({
    url: '/allocation/workloads/' + workloadId + '/' + action,
    method: 'put'
  })
}

// 删除工作负载
export function deleteWorkload(workloadId) {
  return request({
    url: '/allocation/workloads/' + workloadId,
    method: 'delete'
  })
}
