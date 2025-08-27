package com.ruoyi.web.controller.k8s;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kubernetes监控控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/k8s/monitor")
public class K8sMonitorController
{
    @PreAuthorize("@ss.hasPermi('k8s:monitor:list')")
    @GetMapping("/data")
    public AjaxResult getMonitorData()
    {
        Map<String, Object> data = new HashMap<>();
        
        // 集群概览数据
        Map<String, Object> summary = new HashMap<>();
        summary.put("nodes", 3);
        summary.put("pods", 24);
        summary.put("deployments", 8);
        summary.put("errors", 4);
        data.put("summary", summary);
        
        // 节点数据
        List<Map<String, Object>> nodes = new ArrayList<>();
        
        Map<String, Object> node1 = new HashMap<>();
        node1.put("name", "k8s-master-01");
        node1.put("status", "Ready");
        node1.put("cpuUsed", 4.2);
        node1.put("cpuTotal", 8);
        node1.put("cpuPercent", 52.5);
        node1.put("memUsed", 12.8 * 1024);
        node1.put("memTotal", 32 * 1024);
        node1.put("memPercent", 40);
        node1.put("os", "Ubuntu 20.04");
        node1.put("ip", "192.168.152.152");
        node1.put("kubelet", "v1.26.3");
        nodes.add(node1);
        
        Map<String, Object> node2 = new HashMap<>();
        node2.put("name", "k8s-worker-01");
        node2.put("status", "Ready");
        node2.put("cpuUsed", 9.6);
        node2.put("cpuTotal", 16);
        node2.put("cpuPercent", 60);
        node2.put("memUsed", 28.2 * 1024);
        node2.put("memTotal", 64 * 1024);
        node2.put("memPercent", 44);
        node2.put("os", "CentOS 7.9");
        node2.put("ip", "192.168.152.118");
        node2.put("kubelet", "v1.26.3");
        nodes.add(node2);
        
        Map<String, Object> node3 = new HashMap<>();
        node3.put("name", "k8s-worker-02");
        node3.put("status", "NotReady");
        node3.put("cpuUsed", 0);
        node3.put("cpuTotal", 16);
        node3.put("cpuPercent", 0);
        node3.put("memUsed", 0);
        node3.put("memTotal", 64 * 1024);
        node3.put("memPercent", 0);
        node3.put("os", "CentOS 7.9");
        node3.put("ip", "192.168.152.114");
        node3.put("kubelet", "v1.26.3");
        nodes.add(node3);
        
        data.put("nodes", nodes);
        
        // Pod数据
        List<Map<String, Object>> pods = new ArrayList<>();
        
        Map<String, Object> pod1 = new HashMap<>();
        pod1.put("name", "nginx-7b874857bd-5jq8k");
        pod1.put("namespace", "default");
        pod1.put("status", "Running");
        pod1.put("node", "k8s-worker-01");
        pod1.put("restarts", 0);
        pod1.put("age", "2d");
        pods.add(pod1);
        
        Map<String, Object> pod2 = new HashMap<>();
        pod2.put("name", "redis-5f554bd8f4-8n2kz");
        pod2.put("namespace", "database");
        pod2.put("status", "Running");
        pod2.put("node", "k8s-worker-01");
        pod2.put("restarts", 1);
        pod2.put("age", "5d");
        pods.add(pod2);
        
        Map<String, Object> pod3 = new HashMap<>();
        pod3.put("name", "mysql-7c6c5d9f88-p2xw8");
        pod3.put("namespace", "database");
        pod3.put("status", "Running");
        pod3.put("node", "k8s-worker-01");
        pod3.put("restarts", 0);
        pod3.put("age", "3d");
        pods.add(pod3);
        
        Map<String, Object> pod4 = new HashMap<>();
        pod4.put("name", "webapp-85f8f8dc8c-7vxq9");
        pod4.put("namespace", "production");
        pod4.put("status", "Pending");
        pod4.put("node", "");
        pod4.put("restarts", 0);
        pod4.put("age", "10m");
        pods.add(pod4);
        
        Map<String, Object> pod5 = new HashMap<>();
        pod5.put("name", "monitoring-5f7d8f8d7c-2k9w3");
        pod5.put("namespace", "monitoring");
        pod5.put("status", "Failed");
        pod5.put("node", "k8s-master-01");
        pod5.put("restarts", 3);
        pod5.put("age", "1d");
        pods.add(pod5);
        
        Map<String, Object> pod6 = new HashMap<>();
        pod6.put("name", "logging-6d5f8d7f8c-9x2w4");
        pod6.put("namespace", "monitoring");
        pod6.put("status", "Running");
        pod6.put("node", "k8s-master-01");
        pod6.put("restarts", 0);
        pod6.put("age", "1d");
        pods.add(pod6);
        
        data.put("pods", pods);
        
        return AjaxResult.success(data);
    }
}
