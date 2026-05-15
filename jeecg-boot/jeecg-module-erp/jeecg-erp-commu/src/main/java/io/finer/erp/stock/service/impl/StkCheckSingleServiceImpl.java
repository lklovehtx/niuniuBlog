package io.finer.erp.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.stock.entity.StkCheckSingle;
import io.finer.erp.stock.mapper.StkCheckSingleMapper;
import io.finer.erp.stock.service.IStkCheckSingleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 出入库单个体
 * @Author:
 * @Date:   2023-09-17
 * @Version: V1.0
 */
@Service
public class StkCheckSingleServiceImpl
		extends ServiceImpl<StkCheckSingleMapper, StkCheckSingle>
		implements IStkCheckSingleService {
	@Override
	public List<StkCheckSingle> selectByMainId(String mainId) {
		return this.baseMapper.selectByMainId(mainId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByMainId(String mainId) {
		baseMapper.deleteByMainId(mainId);
	}

}
