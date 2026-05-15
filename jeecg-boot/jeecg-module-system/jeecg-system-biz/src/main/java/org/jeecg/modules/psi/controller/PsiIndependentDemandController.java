package org.jeecg.modules.psi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.psi.entity.PsiIndependentDemand;
import org.jeecg.modules.psi.entity.PsiIndependentDemandItem;
import org.jeecg.modules.psi.service.IPsiIndependentDemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Api(tags = "独立需求管理")
@RestController
@RequestMapping("/psi/independentDemand")
@Slf4j
public class PsiIndependentDemandController extends JeecgController<PsiIndependentDemand, IPsiIndependentDemandService> {

    @Autowired
    private IPsiIndependentDemandService psiIndependentDemandService;

    @AutoLog(value = "分页查询独立需求")
    @ApiOperation(value = "分页查询独立需求", notes = "分页查询独立需求")
    @GetMapping(value = "/list")
    public Result<IPage<PsiIndependentDemand>> queryPageList(
            @RequestParam(name = "demandNo", required = false) String demandNo,
            @RequestParam(name = "company", required = false) String company,
            @RequestParam(name = "supplierName", required = false) String supplierName,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        Page<PsiIndependentDemand> page = new Page<>(pageNo, pageSize);
        PsiIndependentDemand query = new PsiIndependentDemand();
        query.setDemandNo(demandNo);
        query.setCompany(company);
        query.setSupplierName(supplierName);
        IPage<PsiIndependentDemand> pageList = psiIndependentDemandService.queryPageList(page, query);
        return Result.OK(pageList);
    }

    @AutoLog(value = "查询独立需求详情")
    @ApiOperation(value = "查询独立需求详情", notes = "查询独立需求详情及行项目")
    @GetMapping(value = "/queryById")
    public Result<PsiIndependentDemand> queryById(@RequestParam(name = "id", required = true) String id) {
        PsiIndependentDemand demand = psiIndependentDemandService.queryById(id);
        return Result.OK(demand);
    }

    @AutoLog(value = "查询行项目列表")
    @ApiOperation(value = "查询行项目列表", notes = "根据需求ID查询行项目")
    @GetMapping(value = "/items")
    public Result<List<PsiIndependentDemandItem>> queryItems(@RequestParam(name = "demandId", required = true) String demandId) {
        List<PsiIndependentDemandItem> items = psiIndependentDemandService.queryItemsByDemandId(demandId);
        return Result.OK(items);
    }

    @AutoLog(value = "添加独立需求")
    @ApiOperation(value = "添加独立需求", notes = "添加独立需求及行项目")
    @PostMapping(value = "/add")
    public Result<PsiIndependentDemand> add(@RequestBody PsiIndependentDemand demand) {
        psiIndependentDemandService.saveDemandWithItems(demand);
        return Result.OK("添加成功！", demand);
    }

    @AutoLog(value = "编辑独立需求")
    @ApiOperation(value = "编辑独立需求", notes = "编辑独立需求及行项目")
    @PutMapping(value = "/edit")
    public Result<PsiIndependentDemand> edit(@RequestBody PsiIndependentDemand demand) {
        psiIndependentDemandService.updateDemandWithItems(demand);
        return Result.OK("修改成功！", demand);
    }

    @AutoLog(value = "删除独立需求")
    @ApiOperation(value = "删除独立需求", notes = "删除独立需求及行项目")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        psiIndependentDemandService.deleteDemandWithItems(id);
        return Result.OK("删除成功!");
    }

    @AutoLog(value = "批量删除独立需求")
    @ApiOperation(value = "批量删除独立需求", notes = "批量删除独立需求及行项目")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        psiIndependentDemandService.deleteBatchDemandWithItems(idList);
        return Result.OK("批量删除成功!");
    }

    @AutoLog(value = "生成需求编号")
    @ApiOperation(value = "生成需求编号", notes = "自动生成独立需求编号")
    @GetMapping(value = "/generateNo")
    public Result<String> generateNo() {
        String demandNo = psiIndependentDemandService.generateDemandNo();
        return Result.OK(demandNo);
    }

    @AutoLog(value = "保存行项目")
    @ApiOperation(value = "保存行项目", notes = "批量保存独立需求行项目")
    @PostMapping(value = "/saveItems")
    public Result<?> saveItems(@RequestBody SaveItemsRequest request) {
        psiIndependentDemandService.saveItems(request.getDemandId(), request.getItems());
        return Result.OK("保存成功!");
    }

    public static class SaveItemsRequest {
        private String demandId;
        private List<PsiIndependentDemandItem> items;

        public String getDemandId() {
            return demandId;
        }

        public void setDemandId(String demandId) {
            this.demandId = demandId;
        }

        public List<PsiIndependentDemandItem> getItems() {
            return items;
        }

        public void setItems(List<PsiIndependentDemandItem> items) {
            this.items = items;
        }
    }
}