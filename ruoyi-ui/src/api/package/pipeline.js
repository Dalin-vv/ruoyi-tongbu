import request from '@/utils/request'

// 查询封装流水线列表
export function listPipelines(query) {
  return request({
    url: '/packages/pipeline/list',
    method: 'get',
    params: query
  })
}

// 查询封装流水线详细
export function getPipeline(pipelineId) {
  return request({
    url: '/packages/pipeline/' + pipelineId,
    method: 'get'
  })
}

// 新增封装流水线
export function addPipeline(data) {
  return request({
    url: '/packages/pipeline',
    method: 'post',
    data: data
  })
}

// 修改封装流水线
export function updatePipeline(data) {
  return request({
    url: '/packages/pipeline',
    method: 'put',
    data: data
  })
}

// 删除封装流水线
export function delPipeline(pipelineId) {
  return request({
    url: '/packages/pipeline/' + pipelineId,
    method: 'delete'
  })
}