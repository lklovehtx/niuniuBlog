package io.finer.erp.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.stock.entity.StkIoSingle;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 出入库单个体
 * @Author:
 * @Date: 20230917
 * @Version: V1.0
 */
public interface IStkIoSingleService extends IService<StkIoSingle> {
	List<StkIoSingle> selectByMainId(String mainId);
	List<StkIoSingle> selectEditingByMainId(String mainId);

	@Transactional(rollbackFor = Exception.class)
	void deleteByMainId(String mainId);
}
