package io.finer.erp.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.stock.entity.StkInventorySingle;
import io.finer.erp.stock.entity.StkIoSingle;
import io.finer.erp.stock.mapper.StkIoSingleMapper;
import io.finer.erp.stock.service.IStkInventorySingleService;
import io.finer.erp.stock.service.IStkIoSingleService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StkIoSingleServiceImpl
		extends ServiceImpl<StkIoSingleMapper, StkIoSingle>
		implements IStkIoSingleService {

	@Autowired
	private IStkInventorySingleService stkInventorySingleService;

	@Override
	public List<StkIoSingle> selectByMainId(String mainId) {
		return this.baseMapper.selectByMainId(mainId);
	}

	@Override
	public List<StkIoSingle> selectEditingByMainId(String mainId) {
		List<StkIoSingle> list = selectByMainId(mainId);
		for(StkIoSingle ios : list) {
			StkInventorySingle inv = stkInventorySingleService.getSingle(ios.getMaterialId(), ios.getSn());
			if (inv != null) {
				ios.setInventoryUnitId(inv.getUnitId());
				ios.setInventoryQty(inv.getQty());
				ios.setInventoryCost(inv.getCost());
			}
		}
		return list;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByMainId(String mainId) {
		baseMapper.deleteByMainId(mainId);
	}

}
