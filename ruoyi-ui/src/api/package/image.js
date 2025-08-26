import request from '@/utils/request'

// 查询基础镜像列表
export function listBaseImages(query) {
  return request({
    url: '/packages/image/base/list',
    method: 'get',
    params: query
  })
}

// 查询基础镜像详细
export function getBaseImage(imageId) {
  return request({
    url: '/packages/image/base/' + imageId,
    method: 'get'
  })
}

// 新增基础镜像
export function addBaseImage(data) {
  return request({
    url: '/packages/image/base',
    method: 'post',
    data: data
  })
}

// 修改基础镜像
export function updateBaseImage(data) {
  return request({
    url: '/packages/image/base',
    method: 'put',
    data: data
  })
}

// 删除基础镜像
export function delBaseImage(imageId) {
  return request({
    url: '/packages/image/base/' + imageId,
    method: 'delete'
  })
}

// 查询服务镜像列表
export function listServiceImages(query) {
  return request({
    url: '/packages/image/service/list',
    method: 'get',
    params: query
  })
}

// 查询服务镜像详细
export function getServiceImage(imageId) {
  return request({
    url: '/packages/image/service/' + imageId,
    method: 'get'
  })
}

// 新增服务镜像
export function addServiceImage(data) {
  return request({
    url: '/packages/image/service',
    method: 'post',
    data: data
  })
}

// 修改服务镜像
export function updateServiceImage(data) {
  return request({
    url: '/packages/image/service',
    method: 'put',
    data: data
  })
}

// 删除服务镜像
export function delServiceImage(imageId) {
  return request({
    url: '/packages/image/service/' + imageId,
    method: 'delete'
  })
}