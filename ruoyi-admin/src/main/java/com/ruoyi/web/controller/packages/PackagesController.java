package com.ruoyi.web.controller.packages;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Packages controller
 */
@RestController
@RequestMapping("/packages")
public class PackagesController {

    // 基础镜像数据
    private List<BaseImage> baseImages = new ArrayList<>();
    // 服务镜像数据
    private List<ServiceImage> serviceImages = new ArrayList<>();
    // 流水线数据
    private List<Pipeline> pipelines = new ArrayList<>();
    // 任务数据
    private List<Task> tasks = new ArrayList<>();

    // 初始化数据
    {
        // 初始化基础镜像数据
        baseImages.add(new BaseImage(1L, "tf-base", "TensorFlow", "2.8"));
        baseImages.add(new BaseImage(2L, "pytorch-base", "PyTorch", "1.12"));

        // 初始化服务镜像数据
        serviceImages.add(new ServiceImage(1L, "cv-service", "v1.0", "租户A"));
        serviceImages.add(new ServiceImage(2L, "nlp-service", "v2.1", "租户B"));

        // 初始化流水线数据
        pipelines.add(new Pipeline(1L, "NLP模型封装", "TensorFlow", "2C4G", "已完成", "", ""));
        pipelines.add(new Pipeline(2L, "CV模型封装", "PyTorch", "4C8G", "运行中", "", ""));

        // 初始化任务数据
        tasks.add(new Task(1L, "NLP流水线执行", "手动", "已完成"));
        tasks.add(new Task(2L, "CV流水线定时任务", "定时", "运行中"));
    }

    // 基础镜像实体类
    public static class BaseImage {
        private Long id;
        private String name;
        private String framework;
        private String version;

        public BaseImage() {}

        public BaseImage(Long id, String name, String framework, String version) {
            this.id = id;
            this.name = name;
            this.framework = framework;
            this.version = version;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getFramework() { return framework; }
        public void setFramework(String framework) { this.framework = framework; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    // 服务镜像实体类
    public static class ServiceImage {
        private Long id;
        private String name;
        private String version;
        private String tenant;

        public ServiceImage() {}

        public ServiceImage(Long id, String name, String version, String tenant) {
            this.id = id;
            this.name = name;
            this.version = version;
            this.tenant = tenant;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getTenant() { return tenant; }
        public void setTenant(String tenant) { this.tenant = tenant; }
    }

    // 流水线实体类
    public static class Pipeline {
        private Long id;
        private String name;
        private String image;
        private String resource;
        private String status;
        private String api;
        private String health;

        public Pipeline() {}

        public Pipeline(Long id, String name, String image, String resource, String status, String api, String health) {
            this.id = id;
            this.name = name;
            this.image = image;
            this.resource = resource;
            this.status = status;
            this.api = api;
            this.health = health;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getApi() { return api; }
        public void setApi(String api) { this.api = api; }
        public String getHealth() { return health; }
        public void setHealth(String health) { this.health = health; }
    }

    // 任务实体类
    public static class Task {
        private Long id;
        private String name;
        private String type;
        private String status;

        public Task() {}

        public Task(Long id, String name, String type, String status) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.status = status;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // 基础镜像管理
    @GetMapping("/image/base/list")
    public TableDataInfo listBaseImages() {
        return getDataTable(baseImages);
    }

    @GetMapping("/image/base/{imageId}")
    public AjaxResult getBaseImage(@PathVariable Long imageId) {
        BaseImage image = baseImages.stream().filter(item -> item.getId().equals(imageId)).findFirst().orElse(null);
        return AjaxResult.success(image);
    }

    @PostMapping("/image/base")
    public AjaxResult addBaseImage(@RequestBody BaseImage image) {
        long maxId = baseImages.stream().mapToLong(BaseImage::getId).max().orElse(0);
        image.setId(maxId + 1);
        baseImages.add(image);
        return AjaxResult.success();
    }

    @PutMapping("/image/base")
    public AjaxResult updateBaseImage(@RequestBody BaseImage image) {
        baseImages.removeIf(item -> item.getId().equals(image.getId()));
        baseImages.add(image);
        return AjaxResult.success();
    }

    @DeleteMapping("/image/base/{imageId}")
    public AjaxResult delBaseImage(@PathVariable Long imageId) {
        baseImages.removeIf(item -> item.getId().equals(imageId));
        return AjaxResult.success();
    }

    // 服务镜像管理
    @GetMapping("/image/service/list")
    public TableDataInfo listServiceImages() {
        return getDataTable(serviceImages);
    }

    @GetMapping("/image/service/{imageId}")
    public AjaxResult getServiceImage(@PathVariable Long imageId) {
        ServiceImage image = serviceImages.stream().filter(item -> item.getId().equals(imageId)).findFirst().orElse(null);
        return AjaxResult.success(image);
    }

    @PostMapping("/image/service")
    public AjaxResult addServiceImage(@RequestBody ServiceImage image) {
        long maxId = serviceImages.stream().mapToLong(ServiceImage::getId).max().orElse(0);
        image.setId(maxId + 1);
        serviceImages.add(image);
        return AjaxResult.success();
    }

    @PutMapping("/image/service")
    public AjaxResult updateServiceImage(@RequestBody ServiceImage image) {
        serviceImages.removeIf(item -> item.getId().equals(image.getId()));
        serviceImages.add(image);
        return AjaxResult.success();
    }

    @DeleteMapping("/image/service/{imageId}")
    public AjaxResult delServiceImage(@PathVariable Long imageId) {
        serviceImages.removeIf(item -> item.getId().equals(imageId));
        return AjaxResult.success();
    }

    // 流水线管理
    @GetMapping("/pipeline/list")
    public TableDataInfo listPipelines() {
        return getDataTable(pipelines);
    }

    @GetMapping("/pipeline/{pipelineId}")
    public AjaxResult getPipeline(@PathVariable Long pipelineId) {
        Pipeline pipeline = pipelines.stream().filter(item -> item.getId().equals(pipelineId)).findFirst().orElse(null);
        return AjaxResult.success(pipeline);
    }

    @PostMapping("/pipeline")
    public AjaxResult addPipeline(@RequestBody Pipeline pipeline) {
        long maxId = pipelines.stream().mapToLong(Pipeline::getId).max().orElse(0);
        pipeline.setId(maxId + 1);
        pipelines.add(pipeline);
        return AjaxResult.success();
    }

    @PutMapping("/pipeline")
    public AjaxResult updatePipeline(@RequestBody Pipeline pipeline) {
        pipelines.removeIf(item -> item.getId().equals(pipeline.getId()));
        pipelines.add(pipeline);
        return AjaxResult.success();
    }

    @DeleteMapping("/pipeline/{pipelineId}")
    public AjaxResult delPipeline(@PathVariable Long pipelineId) {
        pipelines.removeIf(item -> item.getId().equals(pipelineId));
        return AjaxResult.success();
    }

    // 任务管理
    @GetMapping("/task/list")
    public TableDataInfo listTasks() {
        return getDataTable(tasks);
    }

    @GetMapping("/task/{taskId}")
    public AjaxResult getTask(@PathVariable Long taskId) {
        Task task = tasks.stream().filter(item -> item.getId().equals(taskId)).findFirst().orElse(null);
        return AjaxResult.success(task);
    }

    @PostMapping("/task")
    public AjaxResult addTask(@RequestBody Task task) {
        long maxId = tasks.stream().mapToLong(Task::getId).max().orElse(0);
        task.setId(maxId + 1);
        tasks.add(task);
        return AjaxResult.success();
    }

    @PutMapping("/task")
    public AjaxResult updateTask(@RequestBody Task task) {
        tasks.removeIf(item -> item.getId().equals(task.getId()));
        tasks.add(task);
        return AjaxResult.success();
    }

    @DeleteMapping("/task/{taskId}")
    public AjaxResult delTask(@PathVariable Long taskId) {
        tasks.removeIf(item -> item.getId().equals(taskId));
        return AjaxResult.success();
    }

    @PostMapping("/task/execute/{taskId}")
    public AjaxResult executeTask(@PathVariable Long taskId) {
        Task task = tasks.stream().filter(item -> item.getId().equals(taskId)).findFirst().orElse(null);
        if (task != null) {
            task.setStatus("运行中");
        }
        return AjaxResult.success();
    }

    // 工具方法：将List转换为TableDataInfo
    private TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(list.size());
        return rspData;
    }
}