import request from '@/utils/request'

// 查询租户列表
export function listTenant(query) {
  return request({
    url: '/tenant/user/list',
    method: 'get',
    params: query
  })
}

// 查询租户详细
export function getTenant(tenantId) {
  return request({
    url: '/tenant/user/' + tenantId,
    method: 'get'
  })
}

// 新增租户
export function addTenant(data) {
  return request({
    url: '/tenant/user',
    method: 'post',
    data: data
  })
}

// 修改租户
export function updateTenant(data) {
  return request({
    url: '/tenant/user',
    method: 'put',
    data: data
  })
}

// 删除租户
export function delTenant(tenantId) {
  return request({
    url: '/tenant/user/' + tenantId,
    method: 'delete'
  })
}

// 激活租户
export function activateTenant(tenantId) {
  return request({
    url: '/tenant/user/activate/' + tenantId,
    method: 'put'
  })
}

// 暂停租户
export function suspendTenant(tenantId) {
  return request({
    url: '/tenant/user/suspend/' + tenantId,
    method: 'put'
  })
}

// 恢复租户
export function resumeTenant(tenantId) {
  return request({
    url: '/tenant/user/resume/' + tenantId,
    method: 'put'
  })
}

// 停用租户
export function disableTenant(tenantId) {
  return request({
    url: '/tenant/user/disable/' + tenantId,
    method: 'put'
  })
}
