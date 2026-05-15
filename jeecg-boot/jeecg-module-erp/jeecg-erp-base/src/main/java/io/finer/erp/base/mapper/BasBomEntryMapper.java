package io.finer.erp.base.mapper;

import java.util.List;
import io.finer.erp.base.entity.BasBomEntry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: BOM明细
 * @Author: jeecg-boot
 * @Date:   2023-12-22
 * @Version: V1.0
 */
public interface BasBomEntryMapper extends BaseMapper<BasBomEntry> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<BasBomEntry>
   */
	public List<BasBomEntry> selectByMainId(@Param("mainId") String mainId);
}
