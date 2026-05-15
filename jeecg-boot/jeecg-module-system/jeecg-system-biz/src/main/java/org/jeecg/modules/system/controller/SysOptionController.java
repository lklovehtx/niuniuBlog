package org.jeecg.modules.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.system.entity.SysOption;
import org.jeecg.modules.system.service.ISysOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 系统配置
 * @Author: cfm
 * @Date: 2025-12-11
 * @Version: V1.0
 */
@Api(tags = "系统配置")
@RestController
@RequestMapping("/sys/option")
@Slf4j
public class SysOptionController {
    @Autowired
    private ISysOptionService sysOptionService;

    @ApiOperation(value = "系统配置-列表")
    @GetMapping(value = "/list")
    public Result<?> list() {
        return Result.ok(list());
    }

    @ApiOperation(value = "获取系统配置")
    @GetMapping(value = "/options")
    public Result<?> getOptions() {
        return Result.ok(sysOptionService.getOptions());
    }

    @AutoLog(value = "系统配置-新增")
    @ApiOperation(value = "系统配置-新增")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysOption sysOption) {
        sysOptionService.save(sysOption);
        return Result.ok("新增成功！");
    }

    @AutoLog(value = "系统配置-编辑")
    @ApiOperation(value = "系统配置-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody SysOption sysOption) {
        sysOptionService.updateById(sysOption);
        return Result.ok("编辑成功!");
    }

    @AutoLog(value = "系统配置-通过key删除")
    @ApiOperation(value = "系统配置-通过key删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "key", required = true) String key) {
        sysOptionService.removeById(key);
        return Result.ok("删除成功!");
    }

}
