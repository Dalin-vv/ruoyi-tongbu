import request from '@/utils/request'

// 获取主机列表
export function listHosts(query) {
  return request({
    url: '/resource/host/list',
    method: 'get',
    params: query
  })
}

// 获取主机详情
export function getHost(hostId) {
  return request({
    url: '/resource/host/' + hostId,
    method: 'get'
  })
}

// 添加主机
export function addHost(data) {
  return request({
    url: '/resource/host',
    method: 'post',
    data: data
  })
}

// 修改主机
export function updateHost(data) {
  return request({
    url: '/resource/host',
    method: 'put',
    data: data
  })
}

// 删除主机
export function delHost(hostId) {
  return request({
    url: '/resource/host/' + hostId,
    method: 'delete'
  })
}
