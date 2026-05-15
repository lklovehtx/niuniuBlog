package io.finer.erp.base.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.base.entity.BasBizOptions;
import io.finer.erp.base.service.IBasBizOptionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

 /**
 * @Description: bas_biz_options
 * @Author: jeecg-boot
 * @Date:   2022-12-18
 * @Version: V1.1
 */
@Api(tags="bas_biz_options")
@RestController
@RequestMapping("/base/basBizOptions")
@Slf4j
public class BasBizOptionsController extends JeecgController<BasBizOptions, IBasBizOptionsService> {
	@Autowired
	private IBasBizOptionsService basBizOptionsService;

	 /**
	  * 查询
	  *
	  * @param
	  * @return
	  */
	 //@AutoLog(value = "bas_biz_options-查询")
	 @ApiOperation(value="bas_biz_options-查询", notes="bas_biz_options-查询")
	 @GetMapping(value = "/query")
	 public Result<?> query() {
		 BasBizOptions basBizOptions = basBizOptionsService.getOne(Wrappers.emptyWrapper());
		 if(basBizOptions ==null) {
			 return Result.error("未找到数据");
		 }
		 return Result.ok(basBizOptions);
	 }

	 /**
	 *  编辑
	 *
	 * @param basBizOptions
	 * @return
	 */
	@AutoLog(value = "bas_biz_options-编辑")
	@ApiOperation(value="bas_biz_options-编辑", notes="bas_biz_options-编辑")
	@RequiresPermissions("base:biz:options:edit") //20240806 cfm add
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BasBizOptions basBizOptions) {
		basBizOptionsService.updateById(basBizOptions);
		return Result.OK("编辑成功!");
	}

}
