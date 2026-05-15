package io.finer.erp.base.service;

import io.finer.erp.base.entity.BasBomEntry;
import io.finer.erp.base.entity.BasBom;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: BOM
 * @Author: jeecg-boot
 * @Date:   2023-12-22
 * @Version: V1.0
 */
public interface IBasBomService extends IService<BasBom> {

	/**
	 * 添加一对多
	 *
	 * @param basBom
	 * @param basBomEntryList
	 */
	public void saveMain(BasBom basBom,List<BasBomEntry> basBomEntryList) ;
	
	/**
	 * 修改一对多
	 *
   * @param basBom
   * @param basBomEntryList
	 */
	public void updateMain(BasBom basBom,List<BasBomEntry> basBomEntryList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
