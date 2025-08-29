import request from '@/utils/request'

// 获取模板统计信息
export function getTemplateStats() {
  return request({
    url: '/allocation/template/stats',
    method: 'get'
  })
}

// 获取模板列表
export function listTemplates() {
  return request({
    url: '/allocation/template/list',
    method: 'get'
  })
}

// 获取模板详情
export function getTemplate(templateId) {
  return request({
    url: '/allocation/template/' + templateId,
    method: 'get'
  })
}

// 添加模板
export function addTemplate(data) {
  return request({
    url: '/allocation/template',
    method: 'post',
    data: data
  })
}

// 修改模板
export function updateTemplate(data) {
  return request({
    url: '/allocation/template',
    method: 'put',
    data: data
  })
}

// 删除模板
export function deleteTemplate(templateId) {
  return request({
    url: '/allocation/template/' + templateId,
    method: 'delete'
  })
}
