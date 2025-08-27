package com.ruoyi.web.controller.tenant;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 租户用户管理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/tenant/user")
public class TenantUserController extends BaseController
{
    // 模拟静态租户数据
    private List<Tenant> tenants = new ArrayList<>();
    
    public TenantUserController() {
        // 初始化静态数据
        tenants.add(new Tenant(1L, "tenant-production", "张三", "zhangsan@example.com", "2023-05-10", "2023-05-10", "active", 16, 32, "生产环境租户"));
        tenants.add(new Tenant(2L, "tenant-development", "李四", "lisi@example.com", "2023-06-15", "2023-06-15", "active", 8, 16, "开发环境租户"));
        tenants.add(new Tenant(3L, "tenant-testing", "王五", "wangwu@example.com", "2023-07-22", "2023-07-22", "inactive", 4, 8, "测试环境租户"));
        tenants.add(new Tenant(4L, "tenant-archive", "赵六", "zhaoliu@example.com", "2023-02-14", "2023-02-14", "disabled", 0, 0, "归档租户"));
    }

    /**
     * 获取租户列表
     */
    @GetMapping("/list")
    public TableDataInfo list()
    {
        startPage();
        return getDataTable(tenants);
    }

    /**
     * 根据租户编号获取详细信息
     */
    @GetMapping("/{tenantId}")
    public AjaxResult getInfo(@PathVariable("tenantId") Long tenantId)
    {
        Tenant tenant = tenants.stream()
                .filter(t -> t.getId().equals(tenantId))
                .findFirst()
                .orElse(null);
        
        if (tenant == null) {
            return AjaxResult.error("租户不存在");
        }
        
        return AjaxResult.success(tenant);
    }

    /**
     * 新增租户
     */
    @PostMapping
    public AjaxResult add(@RequestBody Tenant tenant)
    {
        Long newId = tenants.stream()
                .mapToLong(Tenant::getId)
                .max()
                .orElse(0L) + 1L;
        
        tenant.setId(newId);
        tenant.setCreateTime(java.time.LocalDate.now().toString());
        tenant.setUpdateTime(java.time.LocalDate.now().toString());
        
        tenants.add(tenant);
        return AjaxResult.success("租户创建成功");
    }

    /**
     * 修改租户
     */
    @PutMapping
    public AjaxResult edit(@RequestBody Tenant tenant)
    {
        for (int i = 0; i < tenants.size(); i++) {
            if (tenants.get(i).getId().equals(tenant.getId())) {
                tenant.setUpdateTime(java.time.LocalDate.now().toString());
                tenants.set(i, tenant);
                return AjaxResult.success("租户信息更新成功");
            }
        }
        return AjaxResult.error("租户不存在");
    }

    /**
     * 删除租户
     */
    @DeleteMapping("/{tenantId}")
    public AjaxResult remove(@PathVariable Long tenantId)
    {
        boolean removed = tenants.removeIf(tenant -> tenant.getId().equals(tenantId));
        if (removed) {
            return AjaxResult.success("租户删除成功");
        } else {
            return AjaxResult.error("租户不存在");
        }
    }

    /**
     * 激活租户
     */
    @PutMapping("/activate/{tenantId}")
    public AjaxResult activateTenant(@PathVariable Long tenantId)
    {
        Tenant tenant = tenants.stream()
                .filter(t -> t.getId().equals(tenantId))
                .findFirst()
                .orElse(null);
        
        if (tenant == null) {
            return AjaxResult.error("租户不存在");
        }
        
        tenant.setStatus("active");
        tenant.setUpdateTime(java.time.LocalDate.now().toString());
        return AjaxResult.success("租户已激活");
    }

    /**
     * 暂停租户
     */
    @PutMapping("/suspend/{tenantId}")
    public AjaxResult suspendTenant(@PathVariable Long tenantId)
    {
        Tenant tenant = tenants.stream()
                .filter(t -> t.getId().equals(tenantId))
                .findFirst()
                .orElse(null);
        
        if (tenant == null) {
            return AjaxResult.error("租户不存在");
        }
        
        tenant.setStatus("suspended");
        tenant.setUpdateTime(java.time.LocalDate.now().toString());
        return AjaxResult.success("租户已暂停");
    }

    /**
     * 恢复租户
     */
    @PutMapping("/resume/{tenantId}")
    public AjaxResult resumeTenant(@PathVariable Long tenantId)
    {
        Tenant tenant = tenants.stream()
                .filter(t -> t.getId().equals(tenantId))
                .findFirst()
                .orElse(null);
        
        if (tenant == null) {
            return AjaxResult.error("租户不存在");
        }
        
        tenant.setStatus("active");
        tenant.setUpdateTime(java.time.LocalDate.now().toString());
        return AjaxResult.success("租户已恢复");
    }

    /**
     * 停用租户
     */
    @PutMapping("/disable/{tenantId}")
    public AjaxResult disableTenant(@PathVariable Long tenantId)
    {
        Tenant tenant = tenants.stream()
                .filter(t -> t.getId().equals(tenantId))
                .findFirst()
                .orElse(null);
        
        if (tenant == null) {
            return AjaxResult.error("租户不存在");
        }
        
        tenant.setStatus("disabled");
        tenant.setUpdateTime(java.time.LocalDate.now().toString());
        return AjaxResult.success("租户已停用");
    }

    /**
     * 租户实体类
     */
    public static class Tenant {
        private Long id;
        private String name;
        private String contact;
        private String email;
        private String createTime;
        private String updateTime;
        private String status;
        private Integer cpu;
        private Integer memory;
        private String description;

        public Tenant() {}

        public Tenant(Long id, String name, String contact, String email, String createTime, String updateTime, 
                     String status, Integer cpu, Integer memory, String description) {
            this.id = id;
            this.name = name;
            this.contact = contact;
            this.email = email;
            this.createTime = createTime;
            this.updateTime = updateTime;
            this.status = status;
            this.cpu = cpu;
            this.memory = memory;
            this.description = description;
        }

        // Getter and Setter methods
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }
        
        public String getUpdateTime() { return updateTime; }
        public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Integer getCpu() { return cpu; }
        public void setCpu(Integer cpu) { this.cpu = cpu; }
        
        public Integer getMemory() { return memory; }
        public void setMemory(Integer memory) { this.memory = memory; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
