package com.ruoyi.web.controller.allocation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源编配控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/allocation")
public class AllocationController
{
    // 静态统计信息
    private static Map<String, Object> stats = new HashMap<>();
    
    // 静态资源编配数据
    private static List<Map<String, Object>> containerResources = new ArrayList<>();
    
    // 静态工作负载编配数据
    private static List<Map<String, Object>> workloads = new ArrayList<>();
    
    // 静态AI模型列表
    private static List<String> aiModels = new ArrayList<>();
    
    // 静态AI加速卡列表
    private static List<String> aiAccelerators = new ArrayList<>();
    
    // 静态计算模板列表（仅名称）
    private static List<String> computeTemplates = new ArrayList<>();
    
    static {
        // 初始化AI模型列表
        aiModels.add("图像识别模型");
        aiModels.add("自然语言处理模型");
        aiModels.add("推荐系统模型");
        aiModels.add("语音识别模型");
        aiModels.add("图像生成模型");
        aiModels.add("视频分析模型");
        
        // 初始化AI加速卡列表
        aiAccelerators.add("NVIDIA V100");
        aiAccelerators.add("NVIDIA A100");
        aiAccelerators.add("NVIDIA T4");
        aiAccelerators.add("华为昇腾910");
        
        // 初始化计算模板列表
        computeTemplates.add("基础CPU模板");
        computeTemplates.add("GPU训练模板");
        computeTemplates.add("高性能计算模板");
        computeTemplates.add("推理服务模板");
        computeTemplates.add("数据处理模板");
        
        // 初始化资源编配数据
        Map<String, Object> resource1 = new HashMap<>();
        resource1.put("name", "模型推理服务");
        resource1.put("cpu", 4);
        resource1.put("memory", 8);
        resource1.put("gpu", 1);
        resource1.put("policy", "高优先级");
        resource1.put("status", "active");
        containerResources.add(resource1);

        Map<String, Object> resource2 = new HashMap<>();
        resource2.put("name", "数据处理服务");
        resource2.put("cpu", 2);
        resource2.put("memory", 4);
        resource2.put("gpu", 0);
        resource2.put("policy", "中优先级");
        resource2.put("status", "active");
        containerResources.add(resource2);

        Map<String, Object> resource3 = new HashMap<>();
        resource3.put("name", "训练任务服务");
        resource3.put("cpu", 8);
        resource3.put("memory", 16);
        resource3.put("gpu", 2);
        resource3.put("policy", "高优先级");
        resource3.put("status", "active");
        containerResources.add(resource3);

        Map<String, Object> resource4 = new HashMap<>();
        resource4.put("name", "批量预测服务");
        resource4.put("cpu", 4);
        resource4.put("memory", 8);
        resource4.put("gpu", 1);
        resource4.put("policy", "中优先级");
        resource4.put("status", "inactive");
        containerResources.add(resource4);

        Map<String, Object> resource5 = new HashMap<>();
        resource5.put("name", "开发测试服务");
        resource5.put("cpu", 2);
        resource5.put("memory", 4);
        resource5.put("gpu", 0);
        resource5.put("policy", "低优先级");
        resource5.put("status", "active");
        containerResources.add(resource5);

        // 初始化工作负载编配数据
        Map<String, Object> workload1 = new HashMap<>();
        workload1.put("id", 1);
        workload1.put("model", "图像识别模型");
        workload1.put("accelerator", "NVIDIA V100");
        workload1.put("template", "GPU训练模板");
        workload1.put("status", "运行中");
        workload1.put("createTime", "2023-06-15 10:30:25");
        workloads.add(workload1);

        Map<String, Object> workload2 = new HashMap<>();
        workload2.put("id", 2);
        workload2.put("model", "自然语言处理模型");
        workload2.put("accelerator", "NVIDIA A100");
        workload2.put("template", "高性能计算模板");
        workload2.put("status", "已停止");
        workload2.put("createTime", "2023-06-16 14:20:18");
        workloads.add(workload2);

        Map<String, Object> workload3 = new HashMap<>();
        workload3.put("id", 3);
        workload3.put("model", "推荐系统模型");
        workload3.put("accelerator", "NVIDIA T4");
        workload3.put("template", "推理服务模板");
        workload3.put("status", "运行中");
        workload3.put("createTime", "2023-06-17 09:15:33");
        workloads.add(workload3);

        Map<String, Object> workload4 = new HashMap<>();
        workload4.put("id", 4);
        workload4.put("model", "语音识别模型");
        workload4.put("accelerator", "华为昇腾910");
        workload4.put("template", "GPU训练模板");
        workload4.put("status", "运行中");
        workload4.put("createTime", "2023-06-18 16:45:12");
        workloads.add(workload4);

        Map<String, Object> workload5 = new HashMap<>();
        workload5.put("id", 5);
        workload5.put("model", "图像生成模型");
        workload5.put("accelerator", "NVIDIA V100");
        workload5.put("template", "高性能计算模板");
        workload5.put("status", "已停止");
        workload5.put("createTime", "2023-06-19 11:30:45");
        workloads.add(workload5);

        // 初始化统计信息
        updateStats();
    }

    /**
     * 更新统计信息
     */
    private static void updateStats() {
        int totalContainers = containerResources.size();
        int runningContainers = (int) containerResources.stream()
                .filter(resource -> "active".equals(resource.get("status")))
                .count();
        int totalModels = aiModels.size();
        int accelerators = aiAccelerators.size();
        
        stats.put("totalContainers", totalContainers);
        stats.put("runningContainers", runningContainers);
        stats.put("totalModels", totalModels);
        stats.put("accelerators", accelerators);
    }

    /**
     * 获取分配统计信息
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:list')")
    @GetMapping("/stats")
    public AjaxResult getAllocationStats()
    {
        return AjaxResult.success(stats);
    }

    /**
     * 获取资源编配列表
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:list')")
    @GetMapping("/resources")
    public AjaxResult listResources()
    {
        return AjaxResult.success(containerResources);
    }

    /**
     * 获取工作负载编配列表
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:list')")
    @GetMapping("/workloads")
    public AjaxResult listWorkloads()
    {
        return AjaxResult.success(workloads);
    }

    /**
     * 获取AI模型列表
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:list')")
    @GetMapping("/ai-models")
    public AjaxResult listAIModels()
    {
        return AjaxResult.success(aiModels);
    }

    /**
     * 获取AI加速卡列表
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:list')")
    @GetMapping("/ai-accelerators")
    public AjaxResult listAIAccelerators()
    {
        return AjaxResult.success(aiAccelerators);
    }

    /**
     * 获取计算模板列表（仅名称）
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:list')")
    @GetMapping("/compute-templates")
    public AjaxResult listComputeTemplates()
    {
        return AjaxResult.success(computeTemplates);
    }

    /**
     * 添加资源限制
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:add')")
    @PostMapping("/resources")
    public AjaxResult addResource(@RequestBody Map<String, Object> resourceData)
    {
        // 检查资源名称是否已存在
        String resourceName = (String) resourceData.get("name");
        boolean exists = containerResources.stream()
                .anyMatch(resource -> resourceName.equals(resource.get("name")));
        
        if (exists) {
            return AjaxResult.error("资源名称已存在");
        }
        
        containerResources.add(new HashMap<>(resourceData));
        updateStats(); // 更新统计信息
        
        return AjaxResult.success("添加成功");
    }

    /**
     * 更新资源限制
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:edit')")
    @PutMapping("/resources")
    public AjaxResult updateResource(@RequestBody Map<String, Object> resourceData)
    {
        String resourceName = (String) resourceData.get("name");
        
        for (int i = 0; i < containerResources.size(); i++) {
            Map<String, Object> resource = containerResources.get(i);
            if (resourceName.equals(resource.get("name"))) {
                containerResources.set(i, resourceData);
                updateStats(); // 更新统计信息
                return AjaxResult.success("修改成功");
            }
        }
        return AjaxResult.error("资源不存在");
    }

    /**
     * 切换资源状态
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:edit')")
    @PutMapping("/resources/{resourceName}/toggle-status")
    public AjaxResult toggleResourceStatus(@PathVariable("resourceName") String resourceName)
    {
        for (Map<String, Object> resource : containerResources) {
            if (resourceName.equals(resource.get("name"))) {
                String currentStatus = (String) resource.get("status");
                String newStatus = "active".equals(currentStatus) ? "inactive" : "active";
                resource.put("status", newStatus);
                updateStats(); // 更新统计信息
                return AjaxResult.success("状态切换成功");
            }
        }
        return AjaxResult.error("资源不存在");
    }

    /**
     * 添加工作负载
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:add')")
    @PostMapping("/workloads")
    public AjaxResult addWorkload(@RequestBody Map<String, Object> workloadData)
    {
        // 生成新的ID
        Long newId = workloads.stream()
                .mapToLong(workload -> Long.parseLong(workload.get("id").toString()))
                .max()
                .orElse(0L) + 1;

        Map<String, Object> newWorkload = new HashMap<>(workloadData);
        newWorkload.put("id", newId);
        newWorkload.put("status", "已停止");
        newWorkload.put("createTime", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        workloads.add(newWorkload);
        updateStats(); // 更新统计信息
        
        return AjaxResult.success("添加成功");
    }

    /**
     * 管理工作负载状态
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:edit')")
    @PutMapping("/workloads/{workloadId}/{action}")
    public AjaxResult manageWorkloadStatus(@PathVariable("workloadId") Long workloadId, 
                                         @PathVariable("action") String action)
    {
        for (Map<String, Object> workload : workloads) {
            if (workloadId.equals(Long.parseLong(workload.get("id").toString()))) {
                if ("start".equals(action)) {
                    workload.put("status", "运行中");
                } else if ("stop".equals(action)) {
                    workload.put("status", "已停止");
                } else {
                    return AjaxResult.error("无效的操作");
                }
                updateStats(); // 更新统计信息
                return AjaxResult.success("操作成功");
            }
        }
        return AjaxResult.error("工作负载不存在");
    }

    /**
     * 删除工作负载
     */
    @PreAuthorize("@ss.hasPermi('allocation:allocation:remove')")
    @DeleteMapping("/workloads/{workloadId}")
    public AjaxResult deleteWorkload(@PathVariable("workloadId") Long workloadId)
    {
        boolean removed = workloads.removeIf(workload -> workloadId.equals(Long.parseLong(workload.get("id").toString())));
        if (removed) {
            updateStats(); // 更新统计信息
            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.error("工作负载不存在");
        }
    }
}
