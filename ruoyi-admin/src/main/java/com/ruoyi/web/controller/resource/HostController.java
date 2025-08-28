package com.ruoyi.web.controller.resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主机管理控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/resource/host")
public class HostController
{
    // 静态主机数据
    private static List<Map<String, Object>> hosts = new ArrayList<>();
    
    static {
        // 初始化静态数据
        Map<String, Object> host1 = new HashMap<>();
        host1.put("id", 1);
        host1.put("name", "GPU-Server-01");
        host1.put("ip", "192.168.1.101");
        host1.put("status", "active");
        host1.put("cpuUsage", 45);
        host1.put("gpuUsage", 78);
        host1.put("hasGpu", true);
        host1.put("memoryTotal", 128);
        host1.put("memoryUsed", 64);
        host1.put("diskTotal", 2048);
        host1.put("diskUsed", 512);
        host1.put("cpuArch", "x86_64");
        host1.put("osVersion", "Ubuntu 20.04");
        host1.put("createTime", "2023-01-15 08:30:45");
        hosts.add(host1);

        Map<String, Object> host2 = new HashMap<>();
        host2.put("id", 2);
        host2.put("name", "CPU-Worker-02");
        host2.put("ip", "192.168.1.102");
        host2.put("status", "active");
        host2.put("cpuUsage", 23);
        host2.put("gpuUsage", 0);
        host2.put("hasGpu", false);
        host2.put("memoryTotal", 64);
        host2.put("memoryUsed", 32);
        host2.put("diskTotal", 1024);
        host2.put("diskUsed", 256);
        host2.put("cpuArch", "x86_64");
        host2.put("osVersion", "CentOS 7.9");
        host2.put("createTime", "2023-02-20 14:22:18");
        hosts.add(host2);

        Map<String, Object> host3 = new HashMap<>();
        host3.put("id", 3);
        host3.put("name", "ARM-Node-01");
        host3.put("ip", "192.168.1.103");
        host3.put("status", "inactive");
        host3.put("cpuUsage", 0);
        host3.put("gpuUsage", 0);
        host3.put("hasGpu", false);
        host3.put("memoryTotal", 32);
        host3.put("memoryUsed", 0);
        host3.put("diskTotal", 512);
        host3.put("diskUsed", 128);
        host3.put("cpuArch", "ARM64");
        host3.put("osVersion", "Kylin V10");
        host3.put("createTime", "2023-03-10 09:45:37");
        hosts.add(host3);

        Map<String, Object> host4 = new HashMap<>();
        host4.put("id", 4);
        host4.put("name", "GPU-Server-02");
        host4.put("ip", "192.168.1.104");
        host4.put("status", "error");
        host4.put("cpuUsage", 92);
        host4.put("gpuUsage", 95);
        host4.put("hasGpu", true);
        host4.put("memoryTotal", 256);
        host4.put("memoryUsed", 230);
        host4.put("diskTotal", 4096);
        host4.put("diskUsed", 1024);
        host4.put("cpuArch", "x86_64");
        host4.put("osVersion", "Ubuntu 22.04");
        host4.put("createTime", "2023-04-05 16:33:29");
        hosts.add(host4);
    }

    /**
     * 获取主机列表
     */
    @PreAuthorize("@ss.hasPermi('resource:host:list')")
    @GetMapping("/list")
    public AjaxResult listHosts()
    {
        return AjaxResult.success(hosts);
    }

    /**
     * 获取主机详情
     */
    @PreAuthorize("@ss.hasPermi('resource:host:query')")
    @GetMapping("/{hostId}")
    public AjaxResult getHost(@PathVariable("hostId") Long hostId)
    {
        for (Map<String, Object> host : hosts) {
            if (host.get("id").equals(hostId)) {
                return AjaxResult.success(host);
            }
        }
        return AjaxResult.error("主机不存在");
    }

    /**
     * 添加主机
     */
    @PreAuthorize("@ss.hasPermi('resource:host:add')")
    @PostMapping
    public AjaxResult addHost(@RequestBody Map<String, Object> hostData)
    {
        // 生成新的ID
        Long newId = hosts.stream()
                .mapToLong(host -> Long.parseLong(host.get("id").toString()))
                .max()
                .orElse(0L) + 1;

        Map<String, Object> newHost = new HashMap<>(hostData);
        newHost.put("id", newId);
        newHost.put("status", "inactive");
        newHost.put("cpuUsage", 0);
        newHost.put("gpuUsage", 0);
        newHost.put("memoryTotal", 64);
        newHost.put("memoryUsed", 0);
        newHost.put("diskTotal", 1024);
        newHost.put("diskUsed", 100);
        newHost.put("createTime", java.time.LocalDateTime.now().toString());

        hosts.add(newHost);
        return AjaxResult.success("添加成功");
    }

    /**
     * 修改主机
     */
    @PreAuthorize("@ss.hasPermi('resource:host:edit')")
    @PutMapping
    public AjaxResult updateHost(@RequestBody Map<String, Object> hostData)
    {
        Long hostId = Long.parseLong(hostData.get("id").toString());
        
        for (int i = 0; i < hosts.size(); i++) {
            Map<String, Object> host = hosts.get(i);
            if (host.get("id").equals(hostId)) {
                // 保留原有的一些字段
                hostData.put("status", host.get("status"));
                hostData.put("cpuUsage", host.get("cpuUsage"));
                hostData.put("gpuUsage", host.get("gpuUsage"));
                hostData.put("memoryTotal", host.get("memoryTotal"));
                hostData.put("memoryUsed", host.get("memoryUsed"));
                hostData.put("diskTotal", host.get("diskTotal"));
                hostData.put("diskUsed", host.get("diskUsed"));
                hostData.put("createTime", host.get("createTime"));
                
                hosts.set(i, hostData);
                return AjaxResult.success("修改成功");
            }
        }
        return AjaxResult.error("主机不存在");
    }

    /**
     * 删除主机
     */
    @PreAuthorize("@ss.hasPermi('resource:host:remove')")
    @DeleteMapping("/{hostId}")
    public AjaxResult deleteHost(@PathVariable("hostId") Long hostId)
    {
        boolean removed = hosts.removeIf(host -> host.get("id").equals(hostId));
        if (removed) {
            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.error("主机不存在");
        }
    }
}
