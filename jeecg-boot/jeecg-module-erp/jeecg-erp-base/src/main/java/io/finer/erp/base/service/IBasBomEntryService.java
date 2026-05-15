package io.finer.erp.base.service;

import io.finer.erp.base.entity.BasBomEntry;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: BOM明细
 * @Author: jeecg-boot
 * @Date:   2023-12-22
 * @Version: V1.0
 */
public interface IBasBomEntryService extends IService<BasBomEntry> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<BasBomEntry>
	 */
	public List<BasBomEntry> selectByMainId(String mainId);
}
