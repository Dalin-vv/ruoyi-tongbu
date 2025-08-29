package com.ruoyi.web.controller.allocation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 算力模板管理控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/allocation/template")
public class ComputeTemplateController
{
    // 静态模板数据
    private static List<Map<String, Object>> computeTemplates = new ArrayList<>();
    
    // 静态统计信息
    private static Map<String, Object> stats = new HashMap<>();
    
    static {
        // 初始化模板数据
        Map<String, Object> template1 = new HashMap<>();
        template1.put("id", 1);
        template1.put("name", "基础CPU模板");
        template1.put("type", "cpu");
        template1.put("cpu", 4);
        template1.put("memory", 8);
        template1.put("gpu", 0);
        template1.put("storage", 100);
        computeTemplates.add(template1);

        Map<String, Object> template2 = new HashMap<>();
        template2.put("id", 2);
        template2.put("name", "GPU训练模板");
        template2.put("type", "gpu");
        template2.put("cpu", 8);
        template2.put("memory", 16);
        template2.put("gpu", 2);
        template2.put("storage", 200);
        computeTemplates.add(template2);

        Map<String, Object> template3 = new HashMap<>();
        template3.put("id", 3);
        template3.put("name", "高性能计算模板");
        template3.put("type", "hybrid");
        template3.put("cpu", 16);
        template3.put("memory", 32);
        template3.put("gpu", 4);
        template3.put("storage", 500);
        computeTemplates.add(template3);

        Map<String, Object> template4 = new HashMap<>();
        template4.put("id", 4);
        template4.put("name", "推理服务模板");
        template4.put("type", "gpu");
        template4.put("cpu", 4);
        template4.put("memory", 8);
        template4.put("gpu", 1);
        template4.put("storage", 150);
        computeTemplates.add(template4);

        Map<String, Object> template5 = new HashMap<>();
        template5.put("id", 5);
        template5.put("name", "数据处理模板");
        template5.put("type", "cpu");
        template5.put("cpu", 8);
        template5.put("memory", 16);
        template5.put("gpu", 0);
        template5.put("storage", 300);
        computeTemplates.add(template5);

        Map<String, Object> template6 = new HashMap<>();
        template6.put("id", 6);
        template6.put("name", "开发测试模板");
        template6.put("type", "cpu");
        template6.put("cpu", 2);
        template6.put("memory", 4);
        template6.put("gpu", 0);
        template6.put("storage", 50);
        computeTemplates.add(template6);

        // 初始化统计信息
        updateStats();
    }

    /**
     * 更新统计信息
     */
    private static void updateStats() {
        int totalTemplates = computeTemplates.size();
        int activeTemplates = totalTemplates; // 假设所有模板都是活跃的
        int cpuTotal = computeTemplates.stream().mapToInt(t -> (int) t.get("cpu")).sum();
        int gpuTotal = computeTemplates.stream().mapToInt(t -> (int) t.get("gpu")).sum();
        
        stats.put("totalTemplates", totalTemplates);
        stats.put("activeTemplates", activeTemplates);
        stats.put("cpuTotal", cpuTotal);
        stats.put("gpuTotal", gpuTotal);
    }

    /**
     * 获取模板统计信息
     */
    @PreAuthorize("@ss.hasPermi('allocation:template:list')")
    @GetMapping("/stats")
    public AjaxResult getTemplateStats()
    {
        return AjaxResult.success(stats);
    }

    /**
     * 获取模板列表
     */
    @PreAuthorize("@ss.hasPermi('allocation:template:list')")
    @GetMapping("/list")
    public AjaxResult listTemplates()
    {
        return AjaxResult.success(computeTemplates);
    }

    /**
     * 获取模板详情
     */
    @PreAuthorize("@ss.hasPermi('allocation:template:query')")
    @GetMapping("/{templateId}")
    public AjaxResult getTemplate(@PathVariable("templateId") Long templateId)
    {
        for (Map<String, Object> template : computeTemplates) {
            if (template.get("id").equals(templateId)) {
                return AjaxResult.success(template);
            }
        }
        return AjaxResult.error("模板不存在");
    }

    /**
     * 添加模板
     */
    @PreAuthorize("@ss.hasPermi('allocation:template:add')")
    @PostMapping
    public AjaxResult addTemplate(@RequestBody Map<String, Object> templateData)
    {
        // 生成新的ID
        Long newId = computeTemplates.stream()
                .mapToLong(template -> Long.parseLong(template.get("id").toString()))
                .max()
                .orElse(0L) + 1;

        Map<String, Object> newTemplate = new HashMap<>(templateData);
        newTemplate.put("id", newId);
        
        computeTemplates.add(newTemplate);
        updateStats(); // 更新统计信息
        
        return AjaxResult.success("添加成功");
    }

    /**
     * 修改模板
     */
    @PreAuthorize("@ss.hasPermi('allocation:template:edit')")
    @PutMapping
    public AjaxResult updateTemplate(@RequestBody Map<String, Object> templateData)
    {
        Long templateId = Long.parseLong(templateData.get("id").toString());
        
        for (int i = 0; i < computeTemplates.size(); i++) {
            Map<String, Object> template = computeTemplates.get(i);
            if (template.get("id").equals(templateId)) {
                computeTemplates.set(i, templateData);
                updateStats(); // 更新统计信息
                return AjaxResult.success("修改成功");
            }
        }
        return AjaxResult.error("模板不存在");
    }

    /**
     * 删除模板
     */
    @PreAuthorize("@ss.hasPermi('allocation:template:remove')")
    @DeleteMapping("/{templateId}")
    public AjaxResult deleteTemplate(@PathVariable("templateId") Long templateId)
    {
        boolean removed = computeTemplates.removeIf(template -> template.get("id").equals(templateId));
        if (removed) {
            updateStats(); // 更新统计信息
            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.error("模板不存在");
        }
    }
}
