package io.finer.erp.stock.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.stock.entity.StkInventorySingle;
import io.finer.erp.stock.service.IStkInventorySingleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 个体即时库存
 * @Author:
 * @Date:   2023-09-10
 * @Version: V1.0
 */
@Api(tags="个体即时库存")
@RestController
@RequestMapping("/stock/stkInventorySingle")
@Slf4j
public class StkInventorySingleController extends JeecgController<StkInventorySingle, IStkInventorySingleService> {

   @Autowired
   private IStkInventorySingleService singleService;

   /**
    * 分页列表查询
    *
    * @param StkInventorySingle
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "个体即时库存-分页列表查询")
   @ApiOperation(value="个体即时库存-分页列表查询", notes="个体即时库存-分页列表查询")
   @GetMapping(value = "/list")
   public Result<?> queryPageList(StkInventorySingle StkInventorySingle,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<StkInventorySingle> queryWrapper = QueryGenerator.initQueryWrapper(StkInventorySingle, req.getParameterMap());
       Page<StkInventorySingle> page = new Page<StkInventorySingle>(pageNo, pageSize);
       IPage<StkInventorySingle> pageList = singleService.page(page, queryWrapper);
       return Result.OK(pageList);
   }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "个体即时库存-通过id查询")
    @ApiOperation(value="个体即时库存-通过id查询", notes="个体即时库存-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
        StkInventorySingle StkInventorySingle = singleService.getById(id);
        if(StkInventorySingle ==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(StkInventorySingle);
    }


    /**
     * 通过sn查询
     *
     * @param materialId
     * @param sn
     * @return
     */
    //@AutoLog(value = "个体即时库存-通过sn查询")
    @ApiOperation(value="个体即时库存-通过sn查询", notes="个体即时库存-通snd查询")
    @GetMapping(value = "/queryBySn")
    public Result<?> queryBySn(@RequestParam String materialId,  @RequestParam String sn) {
        StkInventorySingle StkInventorySingle = singleService.getSingle(materialId, sn);
        if(StkInventorySingle ==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(StkInventorySingle);
    }

    /**
    *  编辑
    *
    * @param StkInventorySingle
    * @return
    */
   @AutoLog(value = "个体即时库存-编辑")
   @ApiOperation(value="个体即时库存-编辑", notes="个体即时库存-编辑")
   @RequiresPermissions("stock:inventory:single:edit") //20240806 cfm add
   @PutMapping(value = "/edit")
   public Result<?> edit(@RequestBody StkInventorySingle StkInventorySingle) {
       singleService.updateById(StkInventorySingle);
       return Result.OK("编辑成功!");
   }

   /**
    *   通过id删除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "个体即时库存-通过id删除")
   @ApiOperation(value="个体即时库存-通过id删除", notes="个体即时库存-通过id删除")
   @RequiresPermissions("stock:inventory:single:delete") //20240806 cfm add
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       singleService.removeById(id);
       return Result.OK("删除成功!");
   }

   /**
    *  批量删除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "个体即时库存-批量删除")
   @ApiOperation(value="个体即时库存-批量删除", notes="个体即时库存-批量删除")
   @RequiresPermissions("stock:inventory:single:delete") //20240806 cfm add
   @DeleteMapping(value = "/deleteBatch")
   public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       this.singleService.removeByIds(Arrays.asList(ids.split(",")));
       return Result.OK("批量删除成功!");
   }

   /**
   * 导出excel
   *
   * @param request
   * @param StkInventorySingle
   */
   @AutoLog(value = "导出为excel")
   @RequiresPermissions("stock:inventory:single:export") //20240806 cfm add
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, StkInventorySingle StkInventorySingle) {
       return super.exportXls(request, StkInventorySingle, StkInventorySingle.class, "个体即时库存");
   }

   /**
     * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
   @AutoLog(value = "通过excel导入数据")
   @RequiresPermissions("stock:inventory:single:import") //20240806 cfm add
   @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
   public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       return super.importExcel(request, response, StkInventorySingle.class);
   }

}
