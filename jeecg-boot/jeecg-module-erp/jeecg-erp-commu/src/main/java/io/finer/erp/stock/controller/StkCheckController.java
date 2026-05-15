package io.finer.erp.stock.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.finer.erp.stock.entity.*;
import io.finer.erp.stock.service.*;
import io.finer.erp.stock.vo.StkCheckPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SqlInjectionUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

 /**
 * @Description: 盘点卡
 * @Author: jeecg-boot
 * @Date:   2020-05-18
 * @Version: V1.0
 */
@Api(tags="盘点卡")
@RestController
@RequestMapping("/stock/stkCheck")
@Slf4j
public class StkCheckController {
	@Autowired
	private IStkCheckService stkCheckService;
	@Autowired
	private IStkCheckEntryService stkCheckEntryService;
	 @Autowired
	 private IStkCheckSingleService stkCheckSingleService;
	@Autowired
	private IStkInventoryService stkInventoryService;
	@Autowired
	private IStkInventorySingleService stkInventorySingleService;

	/**
	 * 分页列表查询
	 *
	 * @param stkCheck
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "盘点卡-分页列表查询")
	@ApiOperation(value="盘点卡-分页列表查询", notes="盘点卡-分页列表查询")
	@GetMapping(value = {"/list", "/list/{hasSingle}"})
	@PermissionData(pageComponent="stock/stkCheck/list") //20240531 cfm add
	public Result<?> queryPageList(StkCheck stkCheck,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		if (stkCheck.getHasSingle() == null) {
			stkCheck.setHasSingle(0);
		}
		QueryWrapper<StkCheck> queryWrapper = QueryGenerator.initQueryWrapper(stkCheck, req.getParameterMap());
		Page<StkCheck> page = new Page<StkCheck>(pageNo, pageSize);
		IPage<StkCheck> pageList = stkCheckService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "盘点卡-通过id查询")
	 @ApiOperation(value="盘点卡-通过id查询", notes="盘点卡-通过id查询")
	 @GetMapping(value = {"/queryById", "/queryById/dictText"})
	 public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		 StkCheck stkCheck = stkCheckService.getById(id);
		 if(stkCheck ==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(stkCheck);
	 }

	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "明细通过主表ID查询")
	 @ApiOperation(value="明细通过主表ID查询", notes="明细-通过主表ID查询")
	 @GetMapping(value = {"/queryEntryByMainId", "/queryEntryByMainId/dictText"})
	 public Result<?> queryEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<StkCheckEntry> stkCheckEntryList = stkCheckEntryService.selectByMainId(id);
		 return Result.OK(stkCheckEntryList);
	 }

	 //20231109 cfm add
	 //@AutoLog(value = "个体通过主表ID查询")
	 @ApiOperation(value="个体通过主表ID查询", notes="个体-通过主表ID查询")
	 @GetMapping(value = {"/querySingleByMainId", "/querySingleByMainId/dictText"})
	 public Result<?> querySingleListByMainId(@RequestParam(name="id",required=true) String id) {
		 List<StkCheckSingle> list = stkCheckSingleService.selectByMainId(id);
		 return Result.OK(list);
	 }

	 //@AutoLog(value = "明细通过盘点范围查询")
	 @ApiOperation(value="明细通过盘点范围查询", notes="明细-通过盘点范围查询")
	 @GetMapping(value = "/queryEntryByRange")
	 public Result<?> queryEntryListByRange(StkCheck stkCheck, HttpServletRequest req) {
		 //20240113 cfm add
		 SqlInjectionUtil.filterContent(stkCheck.getWarehouseId(), SymbolConstant.SINGLE_QUOTATION_MARK);
		 SqlInjectionUtil.filterContent(stkCheck.getMaterialCategoryId(), SymbolConstant.SINGLE_QUOTATION_MARK);

		 QueryWrapper<StkInventory> queryWrapper = new QueryWrapper<>();
		 queryWrapper.eq("is_closed", 0);

		 //20231217 cfm modi
		 String id = stkCheck.getWarehouseId();
		 if (!StringUtils.isEmpty(id)) {
			 String sql = "SELECT id FROM bas_warehouse " +
					 " WHERE `code` LIKE (SELECT CONCAT(`code`, '%%') " +
					 " FROM bas_warehouse WHERE id = '%s')"; //仓库必须是层次代码
			 queryWrapper.inSql("warehouse_id", String.format(sql, id));
		 }

		 id = stkCheck.getMaterialCategoryId();
		 if (!StringUtils.isEmpty(id)) {
			 String sql = "SELECT m.id " +
					 "  FROM bas_material_category c JOIN bas_material m ON c.id = m.category_id " +
					 " WHERE c.code LIKE (SELECT CONCAT(`code`, '%%') FROM bas_material_category WHERE id = '%s')"; //物料种类必须是层次代码
			 queryWrapper.inSql("material_id", String.format(sql, id));
		 }

		 List<StkInventory> invList = stkInventoryService.list(queryWrapper);
		 List<StkCheckEntry> checkList = new ArrayList<>();
		 int i = 1;
		 for(StkInventory inv:invList) {
			 StkCheckEntry check = new StkCheckEntry();
			 BeanUtils.copyProperties(inv, check,
					 "id", "qty", "remark", "custom1", "custom2", "version");
			 check.setEntryNo(i++);
			 check.setIsNewBatch(0);
			 check.setBookQty(inv.getQty());

			 //20231108 cfm modi：如要有盈亏的才需录入，可在前端初始为“实存数量=账存数量”
			 // check.setQty(inv.getQty()); //初始为“实存数量=账存数量”，有盈亏的才需录入
			 check.setQty(null);

			 check.setProfitQty(BigDecimal.ZERO);
			 checkList.add(check);
		 }
		 return Result.OK(checkList);
	 }

	 //20231031 cfm add
	 //@AutoLog(value = "个体通过盘点范围查询")
	 @ApiOperation(value="个体通过盘点范围查询", notes="个体通过盘点范围查询")
	 @GetMapping(value = "/querySingleByRange")
	 public Result<?> querySingleListByRange(StkCheck stkCheck, HttpServletRequest req) {
		 //20240113 cfm add
		 SqlInjectionUtil.filterContent(stkCheck.getWarehouseId(), SymbolConstant.SINGLE_QUOTATION_MARK);
		 SqlInjectionUtil.filterContent(stkCheck.getMaterialCategoryId(), SymbolConstant.SINGLE_QUOTATION_MARK);

		 QueryWrapper<StkInventorySingle> queryWrapper = new QueryWrapper<>();
         queryWrapper.ne("qty", 0);

		 String id = stkCheck.getWarehouseId();
		 if (!StringUtils.isEmpty(id)) {
			 String sql = "SELECT id FROM bas_warehouse " +
					 " WHERE `code` LIKE (SELECT CONCAT(`code`, '%%') " +
					 " FROM bas_warehouse WHERE id = '%s')"; //仓库必须是层次代码
			 queryWrapper.inSql("warehouse_id", String.format(sql, id));
		 }

		 id = stkCheck.getMaterialCategoryId();
		 if (!StringUtils.isEmpty(id)) {
			 String sql = "SELECT m.id " +
					 "  FROM bas_material_category c JOIN bas_material m ON c.id = m.category_id " +
					 " WHERE c.code LIKE (SELECT CONCAT(`code`, '%%') FROM bas_material_category WHERE id = '%s')"; //物料种类必须是层次代码
			 queryWrapper.inSql("material_id", String.format(sql, id));
		 }

		 List<StkInventorySingle> invList = stkInventorySingleService.list(queryWrapper);
		 List<StkCheckSingle> checkList = new ArrayList<>();
		 for(StkInventorySingle inv:invList) {
			 StkCheckSingle check = new StkCheckSingle();
			 BeanUtils.copyProperties(inv, check,
					 "id", "qty", "remark", "custom1", "custom2", "version");
			 check.setIsNewSn(0);
			 check.setBookQty(inv.getQty());
			 checkList.add(check);
		 }
		 return Result.OK(checkList);
	 }

	 /**
	 *   新增
	 *
	 * @param stkCheckPage
	 * @return
	 */
	@AutoLog(value = "盘点卡-新增")
	@ApiOperation(value="盘点卡-新增", notes="盘点卡-新增")
	@RequiresPermissions("stock:check:add") //20240806 cfm add
	@PostMapping(value = "/add/{action}")
	public Result<?> add(@RequestBody StkCheckPage stkCheckPage, @PathVariable String action) {
		StkCheck bill = new StkCheck();
		BeanUtils.copyProperties(stkCheckPage, bill);
		try {
			if (action.equals("submit")) {
				stkCheckService.submitAdd(bill, stkCheckPage.getStkCheckEntryList(), stkCheckPage.getStkCheckSingleList());
				return Result.OK("新增提交成功！");
			} else {
				stkCheckService.saveAdd(bill, stkCheckPage.getStkCheckEntryList(), stkCheckPage.getStkCheckSingleList());
				return Result.OK("新增保存成功！");
			}
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  编辑
	 *
	 * @param stkCheckPage
	 * @return
	 */
	@AutoLog(value = "盘点卡-编辑")
	@ApiOperation(value="盘点卡-编辑", notes="盘点卡-编辑")
	@RequiresPermissions("stock:check:edit") //20240806 cfm add
	@PutMapping(value = "/edit/{action}")
	public Result<?> edit(@RequestBody StkCheckPage stkCheckPage, @PathVariable String action) {
		StkCheck bill = new StkCheck();
		BeanUtils.copyProperties(stkCheckPage, bill);
		try {
			if (action.equals("submit")) {
				stkCheckService.submitEdit(bill, stkCheckPage.getStkCheckEntryList(), stkCheckPage.getStkCheckSingleList());
				return Result.OK("编辑提交成功!");
			} else {
				stkCheckService.saveEdit(bill, stkCheckPage.getStkCheckEntryList(), stkCheckPage.getStkCheckSingleList());
				return Result.OK("编辑保存成功!");
			}

		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "盘点卡-通过id删除")
	@ApiOperation(value="盘点卡-通过id删除", notes="盘点卡-通过id删除")
	@RequiresPermissions("stock:check:delete") //20240806 cfm add
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			stkCheckService.delete(id);
			return Result.OK("单据删除成功!");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "盘点卡-批量删除")
	@ApiOperation(value="盘点卡-批量删除", notes="盘点卡-批量删除")
	@RequiresPermissions("stock:check:delete") //20240806 cfm add
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		try {
			this.stkCheckService.delete(Arrays.asList(ids.split(",")));
			return Result.OK("批量删除成功！");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	 @AutoLog(value = "盘点卡-审核")
	 @ApiOperation(value="盘点卡-审核", notes="盘点卡-审核")
	 @RequiresPermissions("stock:check:check") //20240806 cfm add
	 @PutMapping(value = "/check")
	 public Result<?> check(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.check(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK( "审核提交成功!");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-发起审批")
	 @ApiOperation(value="盘点卡-发起审批", notes="盘点卡-发起审批")
	 @PutMapping(value = "/bpm/start")
	 public Result<?> startBpmInstance(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.startBpmInstance(json.getString("id"));
			 return Result.OK("发起审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-结束审批")
	 @ApiOperation(value="盘点卡-结束审批", notes="盘点卡-结束审批")
	 @RequiresPermissions("stock:check:bpm:end") //20240806 cfm add
	 @PutMapping(value = "/bpm/end")
	 public Result<?> bpmInstanceManualEnd(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.bpmInstanceEnd(json.getString("id"),
					 json.getString("approvalResultType"),
					 json.getString("approvalRemark"));
			 return Result.OK("结束审批成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-执行")
	 @ApiOperation(value="盘点卡-执行", notes="盘点卡-执行")
	 @RequiresPermissions("stock:check:execute") //20240806 cfm add
	 @PutMapping(value = "/execute")
	 public Result<?> execute(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.execute(json.getString("id"));
			 return Result.OK("单据执行成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-关闭")
	 @ApiOperation(value="盘点卡-关闭", notes="盘点卡-关闭")
	 @RequiresPermissions("stock:check:close") //20240806 cfm add
	 @PutMapping(value = "/close")
	 public Result<?> close(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.close(json.getString("id"));
			 return Result.OK("关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡--反关闭")
	 @ApiOperation(value="盘点卡--反关闭", notes="盘点卡--反关闭")
	 @RequiresPermissions("stock:check:unclose") //20240806 cfm add
	 @PutMapping(value = "/unclose")
	 public Result<?> unclose(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.unclose(json.getString("id"));
			 return Result.OK("反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  *  批量关闭
	  *
	  * @param json
	  * @return
	  */
	 @AutoLog(value = "盘点卡-批量关闭")
	 @ApiOperation(value="盘点卡-批量关闭", notes="盘点卡-批量关闭")
	 @RequiresPermissions("stock:check:close") //20240806 cfm add
	 @PutMapping(value = "/closeBatch")
	 public Result<String> closeBatch(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.close(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 /**
	  *  批量反关闭
	  *
	  * @param json
	  * @return
	  */
	 @AutoLog(value = "盘点卡-批量反关闭")
	 @ApiOperation(value="盘点卡-批量反关闭", notes="盘点卡-批量反关闭")
	 @RequiresPermissions("stock:check:unclose") //20240806 cfm add
	 @PutMapping(value = "/uncloseBatch")
	 public Result<String> uncloseBatch(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.unclose(Arrays.asList(json.getString("ids").split(",")));
			 return Result.OK("批量反关闭成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

	 @AutoLog(value = "盘点卡-作废")
	 @ApiOperation(value="盘点卡-作废", notes="盘点卡-作废")
	 @RequiresPermissions("stock:check:void") //20240806 cfm add
	 @PutMapping(value = "/void")
	 public Result<?> voidBill(@RequestBody JSONObject json) {
		 try {
			 stkCheckService.voidBill(json.getString("id"));
			 return Result.OK("作废成功！");
		 } catch (Exception e) {
			 return Result.error(e.getMessage());
		 }
	 }

    /**
    * 导出为excel
    *
    * @param request
    * @param stkCheck
    */
	//20231217 cfm modi: 增加 hasSingle
	@AutoLog(value = "导出为excel")
	@RequiresPermissions("stock:check:export") //20240806 cfm add
	@GetMapping(value = {"/exportXls", "/exportXls/{hasSingle}"})
    public ModelAndView exportXls(HttpServletRequest request, StkCheck stkCheck) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<StkCheck> queryWrapper = QueryGenerator.initQueryWrapper(stkCheck, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<StkCheck> queryList = stkCheckService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<StkCheck> stkCheckList = new ArrayList<StkCheck>();
      if(oConvertUtils.isEmpty(selections)) {
          stkCheckList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          stkCheckList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<StkCheckPage> pageList = new ArrayList<StkCheckPage>();
      for (StkCheck main : stkCheckList) {
          StkCheckPage vo = new StkCheckPage();
          BeanUtils.copyProperties(main, vo);
          List<StkCheckEntry> stkCheckEntryList = stkCheckEntryService.selectByMainId(main.getId());
          vo.setStkCheckEntryList(stkCheckEntryList);

		  //20231217 cfm add
		  if (main.getHasSingle() == 1) {
			  List<StkCheckSingle> stkCheckSingleList = stkCheckSingleService.selectByMainId(main.getId());
			  vo.setStkCheckSingleList(stkCheckSingleList);
		  }
		  else { //20240221 cfm add
			  vo.setStkCheckSingleList(new ArrayList<>());
		  }

          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "盘点卡列表");
      mv.addObject(NormalExcelConstants.CLASS, StkCheckPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("盘点卡数据", "导出人:"+sysUser.getRealname(), "盘点卡"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param hasSingle
    * @return
    */
	//20231217 cfm modi: 增加 hasSingle
	@AutoLog(value = "通过excel导入数据")
	@RequiresPermissions("stock:check:import") //20240806 cfm add
	@RequestMapping(value = {"/importExcel", "/importExcel/{hasSingle}"}, method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, @PathVariable(required = false) Integer hasSingle) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<StkCheckPage> list = ExcelImportUtil.importExcel(file.getInputStream(), StkCheckPage.class, params);

				//20231217 cfm modi
				Map<StkCheck, List<StkCheckEntry>> billMap = new HashMap<>();
				Map<StkCheck, List<StkCheckSingle>> billMap2 = new HashMap<>();
				for (StkCheckPage page : list) {
					//去掉空白的子表记录
					List<StkCheckEntry> entryList = page.getStkCheckEntryList();
					for (int i = entryList.size() - 1; i >= 0; i--) {
						if (StringUtils.isEmpty(entryList.get(i).getMaterialId())) {
							entryList.remove(i);
						}
					}
					List<StkCheckSingle> singleList = page.getStkCheckSingleList();
					for (int i = singleList.size() - 1; i >= 0; i--) {
						if (StringUtils.isEmpty(singleList.get(i).getMaterialId())) {
							singleList.remove(i);
						}
					}

					StkCheck bill = new StkCheck();
					BeanUtils.copyProperties(page, bill);
					if (hasSingle != null && !bill.getHasSingle().equals(hasSingle)) {
						throw new JeecgBootException(bill.getBillNo() + "：“是否有个体”不符！");
					}
					billMap.put(bill, page.getStkCheckEntryList());
					billMap2.put(bill, page.getStkCheckSingleList());
				}
				stkCheckService.saveAddBatch(billMap, billMap2); //20211204 cfm：事务化

				return Result.OK("文件导入成功！数据行数:" + list.size());
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				return Result.error("文件导入失败:"+e.getMessage());
			}
			finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.OK("文件导入失败！");
    }

 }
