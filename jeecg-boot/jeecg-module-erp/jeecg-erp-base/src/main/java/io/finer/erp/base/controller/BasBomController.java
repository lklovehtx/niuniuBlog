package io.finer.erp.base.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import io.finer.erp.base.entity.BasBomEntry;
import io.finer.erp.base.entity.BasBom;
import io.finer.erp.base.vo.BasBomPage;
import io.finer.erp.base.service.IBasBomService;
import io.finer.erp.base.service.IBasBomEntryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: BOM
 * @Author: jeecg-boot
 * @Date:   2023-12-22
 * @Version: V1.0
 */
@Api(tags="BOM")
@RestController
@RequestMapping("/base/basBom")
@Slf4j
public class BasBomController {
	@Autowired
	private IBasBomService basBomService;
	@Autowired
	private IBasBomEntryService basBomEntryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param basBom
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "BOM-分页列表查询")
	@ApiOperation(value="BOM-分页列表查询", notes="BOM-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BasBom>> queryPageList(BasBom basBom,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<BasBom> queryWrapper = QueryGenerator.initQueryWrapper(basBom, req.getParameterMap());
		Page<BasBom> page = new Page<BasBom>(pageNo, pageSize);
		IPage<BasBom> pageList = basBomService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param basBomPage
	 * @return
	 */
	@AutoLog(value = "BOM-添加")
	@ApiOperation(value="BOM-添加", notes="BOM-添加")
	@RequiresPermissions("base:bom:add") //20240806 cfm add
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BasBomPage basBomPage) {
		BasBom basBom = new BasBom();
		BeanUtils.copyProperties(basBomPage, basBom);
		basBomService.saveMain(basBom, basBomPage.getBasBomEntryList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param basBomPage
	 * @return
	 */
	@AutoLog(value = "BOM-编辑")
	@ApiOperation(value="BOM-编辑", notes="BOM-编辑")
	@RequiresPermissions("base:bom:edit") //20240806 cfm add
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BasBomPage basBomPage) {
		BasBom basBom = new BasBom();
		BeanUtils.copyProperties(basBomPage, basBom);
		BasBom basBomEntity = basBomService.getById(basBom.getId());
		if(basBomEntity==null) {
			return Result.error("未找到对应数据");
		}
		basBomService.updateMain(basBom, basBomPage.getBasBomEntryList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "BOM-通过id删除")
	@ApiOperation(value="BOM-通过id删除", notes="BOM-通过id删除")
	@RequiresPermissions("base:bom:delete") //20240806 cfm add
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		basBomService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "BOM-批量删除")
	@ApiOperation(value="BOM-批量删除", notes="BOM-批量删除")
	@RequiresPermissions("base:bom:delete") //20240806 cfm add
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.basBomService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "BOM-通过id查询")
	@ApiOperation(value="BOM-通过id查询", notes="BOM-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BasBom> queryById(@RequestParam(name="id",required=true) String id) {
		BasBom basBom = basBomService.getById(id);
		if(basBom==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(basBom);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "BOM明细通过主表ID查询")
	@ApiOperation(value="BOM明细主表ID查询", notes="BOM明细-通主表ID查询")
	@GetMapping(value = "/queryEntryByMainId")
	public Result<List<BasBomEntry>> queryBasBomEntryListByMainId(@RequestParam(name="id",required=true) String id) {
		List<BasBomEntry> basBomEntryList = basBomEntryService.selectByMainId(id);
		return Result.OK(basBomEntryList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param basBom
    */
	@RequiresPermissions("base:bom:export") //20240806 cfm add
	@RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BasBom basBom) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<BasBom> queryWrapper = QueryGenerator.initQueryWrapper(basBom, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
      String selections = request.getParameter("selections");
      if(oConvertUtils.isNotEmpty(selections)) {
         List<String> selectionList = Arrays.asList(selections.split(","));
         queryWrapper.in("id",selectionList);
      }
      //Step.2 获取导出数据
      List<BasBom> basBomList = basBomService.list(queryWrapper);

      // Step.3 组装pageList
      List<BasBomPage> pageList = new ArrayList<BasBomPage>();
      for (BasBom main : basBomList) {
          BasBomPage vo = new BasBomPage();
          BeanUtils.copyProperties(main, vo);
          List<BasBomEntry> basBomEntryList = basBomEntryService.selectByMainId(main.getId());
          vo.setBasBomEntryList(basBomEntryList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "BOM列表");
      mv.addObject(NormalExcelConstants.CLASS, BasBomPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("BOM数据", "导出人:"+sysUser.getRealname(), "BOM"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
	@RequiresPermissions("base:bom:import") //20240806 cfm add
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // 获取上传文件对象
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<BasBomPage> list = ExcelImportUtil.importExcel(file.getInputStream(), BasBomPage.class, params);
              for (BasBomPage page : list) {
                  BasBom po = new BasBom();
                  BeanUtils.copyProperties(page, po);
                  basBomService.saveMain(po, page.getBasBomEntryList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
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
