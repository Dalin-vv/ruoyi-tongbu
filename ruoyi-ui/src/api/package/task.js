import request from '@/utils/request'

// 查询封装任务列表
export function listTasks(query) {
  return request({
    url: '/packages/task/list',
    method: 'get',
    params: query
  })
}

// 查询封装任务详细
export function getTask(taskId) {
  return request({
    url: '/packages/task/' + taskId,
    method: 'get'
  })
}

// 新增封装任务
export function addTask(data) {
  return request({
    url: '/packages/task',
    method: 'post',
    data: data
  })
}

// 修改封装任务
export function updateTask(data) {
  return request({
    url: '/packages/task',
    method: 'put',
    data: data
  })
}

// 删除封装任务
export function delTask(taskId) {
  return request({
    url: '/packages/task/' + taskId,
    method: 'delete'
  })
}

// 执行封装任务
export function executeTask(taskId) {
  return request({
    url: '/packages/task/execute/' + taskId,
    method: 'post'
  })
}