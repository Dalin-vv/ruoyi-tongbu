import request from '@/utils/request'

// 获取租户列表
export function listTenant(query) {
  return request({
    url: '/k8s/tenant/list',
    method: 'get',
    params: query
  })
}

// 获取租户详细信息
export function getTenant(tenantId) {
  return request({
    url: '/k8s/tenant/' + tenantId,
    method: 'get'
  })
}

// 新增租户
export function addTenant(data) {
  return request({
    url: '/k8s/tenant',
    method: 'post',
    data: data
  })
}

// 修改租户
export function updateTenant(data) {
  return request({
    url: '/k8s/tenant',
    method: 'put',
    data: data
  })
}

// 删除租户
export function delTenant(tenantId) {
  return request({
    url: '/k8s/tenant/' + tenantId,
    method: 'delete'
  })
}

// 激活租户
export function activateTenant(tenantId) {
  return request({
    url: '/k8s/tenant/activate/' + tenantId,
    method: 'put'
  })
}

// 停用租户
export function deactivateTenant(tenantId) {
  return request({
    url: '/k8s/tenant/deactivate/' + tenantId,
    method: 'put'
  })
}

// 导出租户
export function exportTenant(query) {
  return request({
    url: '/k8s/tenant/export',
    method: 'get',
    params: query
  })
}
