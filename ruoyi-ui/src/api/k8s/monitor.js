import request from '@/utils/request'

// 获取Kubernetes集群监控概览数据
export function getK8sMonitorSummary() {
  return request({
    url: '/k8s/monitor/summary',
    method: 'get'
  })
}

// 获取Kubernetes节点监控数据
export function getK8sMonitorNodes() {
  return request({
    url: '/k8s/monitor/nodes',
    method: 'get'
  })
}

// 获取Kubernetes Pod监控数据
export function getK8sMonitorPods() {
  return request({
    url: '/k8s/monitor/pods',
    method: 'get'
  })
}

// 获取完整的Kubernetes监控数据
export function getK8sMonitorData() {
  return request({
    url: '/k8s/monitor/data',
    method: 'get'
  })
}
