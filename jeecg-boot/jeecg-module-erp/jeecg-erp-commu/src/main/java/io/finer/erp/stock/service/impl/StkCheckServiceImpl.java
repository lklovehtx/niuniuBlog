package io.finer.erp.stock.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.common.util.BillUtils;
import io.finer.erp.stock.entity.*;
import io.finer.erp.stock.mapper.StkCheckEntryMapper;
import io.finer.erp.stock.mapper.StkCheckMapper;
import io.finer.erp.stock.service.IStkCheckService;
import io.finer.erp.stock.service.IStkCheckSingleService;
import io.finer.erp.stock.service.IStkIoService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 盘点卡
 * @Author:
 * @Date:
 * @Version: V1.0
 */
@Service
public class StkCheckServiceImpl
		extends BillWithEntryServiceImpl<StkCheckMapper, StkCheck, StkCheckEntryMapper, StkCheckEntry>
		implements IStkCheckService {

	@Autowired
	private IStkIoService stkIoService;

	//begin-20231031 cfm add
	@Autowired
	private IStkCheckSingleService stkCheckSingleService;

	@Override
	protected  void beforePersistAdd(StkCheck bill, List<StkCheckEntry> entryList){
		if (bill.getHasSingle() == null){
			bill.setHasSingle(0);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAdd(StkCheck bill, List<StkCheckEntry> entryList, List<StkCheckSingle> singleList) throws Exception {
		saveAdd(bill, entryList);
		saveAdd(bill, singleList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void submitAdd(StkCheck bill, List<StkCheckEntry> entryList, List<StkCheckSingle> singleList) throws Exception {
		beforeSubmit(bill, entryList);
		if (singleList != null && singleList.size() > 0) {
			String id = IdWorker.getIdStr(bill);
			bill.setId(id);
			saveAdd(bill, singleList); //须在submit前保存(假定子表无外键)，否则单据生效后的处理用到StkIoSingle时却还未保存。
		}
		submitAdd(bill, entryList);
	}

	private void saveAdd(StkCheck bill, List<StkCheckSingle> singleList) {
		if(singleList!=null && singleList.size()>0) {
			for(StkCheckSingle a: singleList) {
				a.setMid(bill.getId());//外键设置
			}
			stkCheckSingleService.saveBatch(singleList);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveEdit(StkCheck bill, List<StkCheckEntry> entryList, List<StkCheckSingle> singleList) throws Exception {
		saveEdit(bill, entryList);
		saveEdit(bill, singleList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void submitEdit(StkCheck bill, List<StkCheckEntry> entryList, List<StkCheckSingle> singleList) throws Exception {
		beforeSubmit(bill, entryList);
		saveEdit(bill, singleList); //须在submit前保存，否则单据生效后的处理用到StkIoSingle时却还未保存。
		submitEdit(bill, entryList);
	}

	//20231210 cfm add
	@Override
	@Transactional(rollbackFor = Exception.class)
    public void saveAddBatch(Map<StkCheck, List<StkCheckEntry>> billMap, Map<StkCheck, List<StkCheckSingle>> billMap2) throws Exception {
		for (Map.Entry<StkCheck,List<StkCheckEntry>> entry : billMap.entrySet()) {
			this.saveAdd(entry.getKey(), entry.getValue(), billMap2.get(entry.getKey()));
		}
    }

    private void saveEdit(StkCheck bill, List<StkCheckSingle> singleList) {
		//子表数据：删除后重新插入
		stkCheckSingleService.deleteByMainId(bill.getId());
		saveAdd(bill, singleList);
	}

	private void beforeSubmit(StkCheck bill, List<StkCheckEntry> entryList) {
		if(entryList==null || entryList.size() == 0) {
			return;
		}

		//20231111 cfm add：
		// · 在老版本（V1.1.8-）中，只有计算属性entry.profitQty，正负表示盈或亏，同一entry不存在部分赢部分亏的情况；
		// · 新版本为解决同一entry下的single有盈有亏，增加entry.lossQty属性，盘盈的前端已到entry.profitQty，盘亏的到entry.lossQty；
		for(StkCheckEntry entry:entryList) {
			if (entry.getProfitQty().compareTo(BigDecimal.ZERO) < 0 || entry.getLossQty() == null) {
				throw new JeecgBootException("盘盈数量<0或盘亏数量为空，可能来自不兼容的老版本前端（v1.1.8-），请联系开发人员！");
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(String id) {
		super.delete(id);
		stkCheckSingleService.deleteByMainId(id);
	}
	//end-20231031 cfm add

	//20250505 cfm add for 内置BPM
	@Override
	public JSONObject getVo(String id) throws Exception {
		JSONObject result = super.getVo(id);
		List<StkCheckSingle> singleList = stkCheckSingleService.selectByMainId(id);
		result.put("singleList", BillUtils.parseDictText(singleList));
		return result;
	}

	@Override
	protected void writeBack(StkCheck bill, boolean reverse) {
		//无回写
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void createSubBill(StkCheck bill) throws Exception {
		if (bill.getIsRubric() == 1) {
			throw new JeecgBootException("库存盘点单不能为红字单据！");
		}

		List<StkCheckEntry> entries = this.entryMapper.selectByMainId(bill.getId());
        List<StkCheckSingle> singles = this.stkCheckSingleService.selectByMainId(bill.getId());

		// 20231101 cfm modi: 增加single的处理

        //后置单据-盘盈入库单、盘亏出库单：自动生成
		List<StkCheckEntry> profitEntries = new ArrayList<>();
        List<StkCheckSingle> profitSingles = new ArrayList<>();
        List<StkCheckEntry> lossEntries = new ArrayList<>();
		List<StkCheckSingle> lossSingles = new ArrayList<>();
		for(StkCheckEntry entry:entries) {
			if (entry.getProfitQty().compareTo(BigDecimal.ZERO) != 0) {
				profitEntries.add(entry);
			}
			if (entry.getLossQty().compareTo(BigDecimal.ZERO) != 0) {
				lossEntries.add(entry);
			}

            for (StkCheckSingle single : singles) {
				BigDecimal delta = single.getQty().subtract(single.getBookQty());
				if (delta.compareTo(BigDecimal.ZERO) != 0 &&
						single.getMaterialId().equals(entry.getMaterialId()) &&
                        single.getBatchNo().equals(entry.getBatchNo()) &&
                        single.getWarehouseId().equals(entry.getWarehouseId()) &&
						single.getIsNewSn().equals(entry.getIsNewBatch())) {
					if (delta.compareTo(BigDecimal.ZERO) > 0) {
						profitSingles.add(single);
					} else {
						lossSingles.add(single);
					}
                }
            }
		}

		if (profitEntries.size() > 0) {
			stkIoService.createInBill(bill, profitEntries, profitSingles);
		}
		if (lossEntries.size() > 0) {
			stkIoService.createOutBill(bill, lossEntries, lossSingles);
		}
	}

	@Override
	protected void voidBillPreprocess(StkCheck bill) throws Exception {
		//后置单据-盘盈入库单、盘亏出库单-自动作废：库存盘点单生效后自动生成的
		List<StkIo> list = stkIoService.listNotVoided(bill.getBillType(), bill.getId());
		for(StkIo stkIo: list) {
			if (stkIo.getSrcBillId().equals(bill.getId()) && stkIo.getIsAuto() == 1) {
				stkIoService.voidBill(stkIo.getId());
			}
		}

		//后置单据-盘盈入库单、盘亏出库单：非自动生成的
		String billNos = stkIoService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}
	}

}
