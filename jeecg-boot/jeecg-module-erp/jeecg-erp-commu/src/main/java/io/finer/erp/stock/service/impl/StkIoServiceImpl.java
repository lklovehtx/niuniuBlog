package io.finer.erp.stock.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.finer.erp.base.entity.BasBizOptions;
import io.finer.erp.base.service.IBasBizOptionsService;
import io.finer.erp.common.service.impl.BillWithEntryServiceImpl;
import io.finer.erp.common.util.BillUtils;
import io.finer.erp.finance.entity.*;
import io.finer.erp.finance.service.*;
import io.finer.erp.stock.entity.*;
import io.finer.erp.stock.mapper.StkIoEntryMapper;
import io.finer.erp.stock.mapper.StkIoMapper;
import io.finer.erp.stock.service.IStkCheckService;
import io.finer.erp.stock.service.IStkInventoryService;
import io.finer.erp.stock.service.IStkIoSingleService;
import io.finer.erp.stock.service.IStkIoService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.FillRuleUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 出入库单
 * @Author:
 * @Date:
 * @Version:
 */
@Service
public class StkIoServiceImpl
		extends BillWithEntryServiceImpl<StkIoMapper, StkIo, StkIoEntryMapper, StkIoEntry>
		implements IStkIoService {

	@Autowired
	private IStkInventoryService stkInventoryService;

	@Lazy
	@Autowired
	private IFinPayableService finPayableService;
	@Lazy
	@Autowired
	private IFinPurInvoiceService finPurInvoiceService;
	@Lazy
	@Autowired
	private IFinPaymentReqService finPaymentReqService;
	@Lazy
	@Autowired
	private IFinPaymentService finPaymentService;

	@Lazy
	@Autowired
	private IFinReceivableService finReceivableService;
	@Lazy
	@Autowired
	private IFinSalInvoiceService finSalInvoiceService;
	@Lazy
	@Autowired
	private IFinReceiptService finReceiptService;
	@Lazy
	@Autowired
	private IFinReceiptReqService finReceiptReqService;

	//20250711 cfm add
	@Autowired
	private IBasBizOptionsService basBizOptionsService;
	@Lazy
	@Autowired
	private IStkCheckService stkCheckService;

	//begin-20230916 cfm add
	@Autowired
	private IStkIoSingleService stkIoSingleService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAdd(StkIo bill, List<StkIoEntry> entryList, List<StkIoSingle> singleList) throws Exception {
		saveAdd(bill, entryList);
		saveAdd(bill, singleList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void submitAdd(StkIo bill, List<StkIoEntry> entryList, List<StkIoSingle> singleList) throws Exception {
		validateLockInventory(); //20250711 cfm add

		if (singleList != null && singleList.size() > 0) {
			String id = IdWorker.getIdStr(bill);
			bill.setId(id);
			saveAdd(bill, singleList); //须在submit前保存(假定子表无外键)，否则单据生效后的处理用到StkIoSingle时却还未保存。
		}
		submitAdd(bill, entryList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveEdit(StkIo bill, List<StkIoEntry> entryList, List<StkIoSingle> singleList) throws Exception {
		saveEdit(bill, entryList);
		saveEdit(bill, singleList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void submitEdit(StkIo bill, List<StkIoEntry> entryList, List<StkIoSingle> singleList) throws Exception {
		validateLockInventory(); //20250711 cfm add

		saveEdit(bill, singleList);  //须在submit前保存，否则单据生效后的处理用到StkIoSingle时却还未保存。
		submitEdit(bill, entryList);
	}

	//20250711 cfm add
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void check(String id, @NotNull String approvalResultType, String approvalRemark) throws Exception {
		validateLockInventory();
		super.check(id, approvalResultType, approvalRemark);
	}

	//20250711 cfm add
	private void validateLockInventory() {
		BasBizOptions basBizOptions = basBizOptionsService.getOne(Wrappers.emptyWrapper());
		if (basBizOptions == null ||
				basBizOptions.getIsLockedWhenCheck() == null ||
				basBizOptions.getIsLockedWhenCheck() == 0) return;

		List<StkCheck> list = stkCheckService.listNotEffective();
		if (list != null && list.size() > 0) throw new JeecgBootException("盘点中（有未作废且未生效的盘点卡），不能提交和核批出入库单！");
	}

	//20231210 cfm add
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAddBatch(Map<StkIo,List<StkIoEntry>> billMap, Map<StkIo, List<StkIoSingle>> billMap2) throws Exception {
		for (Map.Entry<StkIo,List<StkIoEntry>> entry : billMap.entrySet()) {
			this.saveAdd(entry.getKey(), entry.getValue(), billMap2.get(entry.getKey()));
		}
	}

	private void saveAdd(StkIo bill, List<StkIoSingle> singleList) {
		if(singleList!=null && singleList.size()>0) {
			for(StkIoSingle s: singleList) {
				s.setMid(bill.getId());//外键设置
				stkIoSingleService.save(s);
			}
		}
	}

	private void saveEdit(StkIo bill, List<StkIoSingle> singleList) {
		//子表数据：删除后重新插入
		stkIoSingleService.deleteByMainId(bill.getId());
		saveAdd(bill, singleList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(String id) {
		stkIoSingleService.deleteByMainId(id);
		super.delete(id);
	}
	//end-20230916 cfm add

	@Override
	protected  void beforePersistAdd(StkIo bill, List<StkIoEntry> entryList){
		BigDecimal cost = new BigDecimal("0.00");
		BigDecimal settleAmt = new BigDecimal("0.00");
		if(entryList!=null && entryList.size()>0) {
			String supplierId = bill.getSupplierId();
			supplierId = supplierId != null ? supplierId : "";
			for(StkIoEntry entry:entryList) {
				entry.setSupplierId(supplierId);
				if (entry.getCost() != null) {
					cost = cost.add(entry.getCost());
				}
				if (entry.getSettleAmt() != null) {
					settleAmt = settleAmt.add(entry.getSettleAmt());
				}
			}
		}
		bill.setCost(cost);
		bill.setSettleAmt(settleAmt);

		//20230917 cfm add
		if (bill.getHasSingle() == null){
			bill.setHasSingle(0);
		}
	}

	@Override
	protected void beforePersistEdit(StkIo bill, List<StkIoEntry> entryList) {
		this.beforePersistAdd(bill, entryList);
	}

	//20250505 cfm add for 内置BPM
	@Override
	public JSONObject getVo(String id) throws Exception {
		JSONObject result = super.getVo(id);
		List<StkIoSingle> singleList = stkIoSingleService.selectByMainId(id);
		result.put("singleList", BillUtils.parseDictText(singleList));
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void createSubBill(StkIo bill) throws Exception {
		//后置单据-应收应付：自动生成
		if (bill.getHasRp() == 1) {
			if (bill.getStockIoType().startsWith("1")){
				//finPayableService.createPayable(bill, entryList);
				finPayableService.createBill(bill);
			} else if (bill.getStockIoType().startsWith("2")) {
				finReceivableService.createBill(bill);
			}
		}

		//20230206 modi: 移入writeBack()中
		//后置单据-即时库存：更新
		//List<StkIoEntry> entryList = this.entryMapper.selectByMainId(bill.getId());
		//stkInventoryService.updateInventory(bill, entryList, false);
	}

	@Override
	protected boolean hasFinishedExecute(StkIo bill) {
		//后置回写——已结算金额
		int i = bill.getSettledAmt().compareTo(bill.getSettleAmt());
		boolean b = bill.getIsRubric() == 0 ? i >= 0 : i <= 0;

		//	后置回写——已开票金额
		String invoiceType = bill.getInvoiceType();
		if (invoiceType != null && invoiceType.startsWith("1")) {
			i = bill.getInvoicedAmt().compareTo(bill.getSettleAmt());
			b = b && (bill.getIsRubric() == 0 ? i >= 0 : i <= 0);
		}

		return super.hasFinishedExecute(bill) && b;
	}

	@Override
	protected void writeBack(StkIo bill, boolean reverse) {
		List<StkIoEntry> entryList = this.entryMapper.selectByMainId(bill.getId());

		//20230917 cfm add
		List<StkIoSingle> ioSingleList = stkIoSingleService.selectByMainId(bill.getId());

		//20230206 modi: 从writeBack()、voidBillPreprocess()移入
		//更新-即时库存
		stkInventoryService.updateInventory(bill, entryList, ioSingleList, reverse);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	protected void voidBillPreprocess(StkIo bill) throws Exception {
		//BEGIN-20240509 cfm modi for：修复【BUG】采购发票、付款申请未作废时采购入库不应能作废，销售发票、销售退货退款申请未作废时销售出库不应能作废。
		//后置单据-采购发票、采购付款申请、采购付款
		String billNos = null;
		if (bill.getStockIoType().startsWith("1")) {
			billNos = finPurInvoiceService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			if (StringUtils.isEmpty(billNos)) {
				billNos = finPaymentReqService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			}
			if (StringUtils.isEmpty(billNos)) {
				billNos = finPaymentService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			}
		}

		//后置单据-销售发票、销售收款
		if (StringUtils.isEmpty(billNos) && bill.getStockIoType().startsWith("2")) {
			billNos = finSalInvoiceService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			if (StringUtils.isEmpty(billNos)) {
				billNos = finReceiptReqService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			}
			if (StringUtils.isEmpty(billNos)) {
				billNos = finReceiptService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
			}
		}
		//END-20240509 cfm modi for：修复【BUG】采购发票、付款申请未作废时采购入库不应能作废，销售发票、销售退货退款申请未作废时销售出库不应能作废。

		//20250409 cfm add for: 修复【BUG】生产退料、采购退货出库、销售退货入库未作废时，相应的生产领料、采购入库、销售出库不应能作废
		if (StringUtils.isEmpty(billNos)) {
			billNos = getNotVoidedBillNos(bill.getBillType(), bill.getId());
		}

		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}

		//后置单据-应付单、应收单-自动作废：出入库据生效后自动生成的
		if (bill.getStockIoType().startsWith("1")) {
			List<FinPayable> list = finPayableService.listNotVoided(bill.getBillType(), bill.getId());
			if (list != null) {
				for(FinPayable f: list) {
					finPayableService.voidBill(f.getId());
				}
			}
		} else if (bill.getStockIoType().startsWith("2")) {
			List<FinReceivable> list = finReceivableService.listNotVoided(bill.getBillType(), bill.getId());
			if (list != null) {
				for(FinReceivable f: list) {
					finReceivableService.voidBill(f.getId());
				}
			}
		}

		//后置单据-应付单、应收单-不自动作废：非出入库据生效后自动生成的；先做自动作废，否则应该自动作废的也会视为“未作废”
		if (!bill.getStockIoType().startsWith("1")) {
			billNos = finPayableService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		}
		if (StringUtils.isEmpty(billNos) && !bill.getStockIoType().startsWith("2")) {
			billNos = finReceivableService.getNotVoidedBillNos(bill.getBillType(), bill.getId());
		}
		if (!StringUtils.isEmpty(billNos)) {
			throw new JeecgBootException("不能作废！有未作废的后置单据：" + billNos);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
    //20231101 cfm modi: 增加参数 stkCheckSingleList
	public void createInBill(StkCheck stkCheck, List<StkCheckEntry> stkCheckEntryList, List<StkCheckSingle> stkCheckSingleList) throws Exception {
		createBill("102", stkCheck, stkCheckEntryList, stkCheckSingleList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
    //20231101 cfm modi: 增加参数 stkCheckSingleList
	public void createOutBill(StkCheck stkCheck, List<StkCheckEntry> stkCheckEntryList, List<StkCheckSingle> stkCheckSingleList) throws Exception {
		createBill("202", stkCheck, stkCheckEntryList, stkCheckSingleList);
	}

    //20231101 cfm modi: 增加参数 stkCheckSingleList
	private void createBill(String stockIoType, StkCheck stkCheck, List<StkCheckEntry> stkCheckEntryList, List<StkCheckSingle> stkCheckSingleList) throws Exception {
		StkIo bill = new StkIo();
		bill.setStockIoType(stockIoType);
		bill.setIsAuto(1);
		bill.setIsRubric(0);
		bill.setSrcBillType(stkCheck.getBillType());
		bill.setSrcBillId(stkCheck.getId());
		bill.setSrcNo(stkCheck.getBillNo());
		String ruleCode = stockIoType.equals("102") ? "stk_pyrk_bill_no":"stk_pkck_bill_no";
		bill.setBillNo((String) FillRuleUtil.executeRule(ruleCode, null));
		bill.setBillDate(stkCheck.getBillDate()); //注意：如果设为系统日期，有可能小于当前业务期间
		bill.setHandler(stkCheck.getChecker());
		bill.setHasRp(0);
		bill.setHasSwell(0);
		bill.setHasSingle(stkCheck.getHasSingle()); //20231109 cfm add

		List<StkIoEntry> entryList = new ArrayList<>();
		int entryNo = 0;
		for(StkCheckEntry checkEntity: stkCheckEntryList) {
			//20231112 cfm modi
			BigDecimal qty = stockIoType.equals("102") ? checkEntity.getProfitQty() : checkEntity.getLossQty();

			StkIoEntry entry = new StkIoEntry();
			entry.setEntryNo(++entryNo);
			entry.setSrcBillType(stkCheck.getBillType());
			entry.setSrcBillId(stkCheck.getId());
			entry.setSrcEntryId(checkEntity.getId());
			entry.setSrcNo(String.format("%s:%d", checkEntity.getBillNo(), checkEntity.getEntryNo()));
			entry.setWarehouseId(checkEntity.getWarehouseId());
			entry.setMaterialId(checkEntity.getMaterialId());
			entry.setBatchNo(checkEntity.getBatchNo());
			entry.setUnitId(checkEntity.getUnitId());
			entry.setStockIoDirection(stockIoType.substring(0,1));
			entry.setSwellQty(BigDecimal.ZERO);
			entry.setQty(qty);
			entry.setExpense(BigDecimal.ZERO);
			entry.setCost(BigDecimal.ZERO);
			entry.setSupplierId(checkEntity.getSupplierId());
			entryList.add(entry);
		}

        //20231101 cfm add
        List<StkIoSingle> singleList = new ArrayList<>();
        for(StkCheckSingle checkSingle: stkCheckSingleList) {
			BigDecimal qty = checkSingle.getQty().subtract(checkSingle.getBookQty());
			if (stockIoType.equals("202")) { //如果是盘亏出库，则取反
				qty = qty.negate();
			}
            StkIoSingle single = new StkIoSingle();
            single.setStockIoDirection(stockIoType.substring(0,1));
            single.setMaterialId(checkSingle.getMaterialId());
            single.setSn(checkSingle.getSn());
            single.setBatchNo(checkSingle.getBatchNo());
            single.setWarehouseId(checkSingle.getWarehouseId());
            single.setUnitId(checkSingle.getUnitId());
            single.setQty(qty);
            single.setSettleAmt(BigDecimal.ZERO);
            single.setExpense(BigDecimal.ZERO);
            single.setCost(BigDecimal.ZERO);
            singleList.add(single);
        }

        //20231101 cfm modi: 增加参数 singleList
        this.submitAdd(bill, entryList, singleList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void payableCheckWriteBack(List<FinPayableCheckEntry> checkEntryList, boolean reverse) {
		Map<String, StkIo> billMap = new HashMap<>();
		List<FinPayableCheckEntry> checkEntryList1 = new ArrayList<>();
		for(FinPayableCheckEntry writterEntry: checkEntryList) {
			String srcBillType = writterEntry.getSrcBillType();
			BigDecimal writterEntryAmt = writterEntry.getAmt();
			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("StkIo")
					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
				continue;
			}

			//前置条件、预处理
			StkIo bill = writtenBackPreprocess(writterEntry, billMap);

			//数据处理
			if (reverse) {
				writterEntryAmt = writterEntryAmt.negate();
			}
			bill.setSettledAmt(bill.getSettledAmt().add(writterEntryAmt));

			//向前转置：如果是退货，不回写订单（订单可能已关闭、结算毛利润等）
			if (bill.getIsReturned() == 0) {
				writtenBackForward(bill, writterEntry, checkEntryList1, FinPayableCheckEntry.class);
			}
		}

		//后置状态
		for(StkIo bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void receivableCheckWriteBack(List<FinReceivableCheckEntry> checkEntryList, boolean reverse) {
		Map<String, StkIo> billMap = new HashMap<>();
		List<FinReceivableCheckEntry> checkEntryList1 = new ArrayList<>();
		for(FinReceivableCheckEntry writterEntry: checkEntryList) {
			String srcBillType = writterEntry.getSrcBillType();
			BigDecimal writterEntryAmt = writterEntry.getAmt();
			if(StringUtils.isEmpty(srcBillType) || !srcBillType.startsWith("StkIo")
					|| writterEntryAmt == null || writterEntryAmt.compareTo(BigDecimal.ZERO) == 0 ) {
				continue;
			}

			//前置条件、预处理
			StkIo bill = writtenBackPreprocess(writterEntry, billMap);

			//数据处理
			if (reverse) {
				writterEntryAmt = writterEntryAmt.negate();
			}
			bill.setSettledAmt(bill.getSettledAmt().add(writterEntryAmt));

			//向前转置：如果是退货，不回写订单（订单可能已关闭、结算毛利润等）
			if (bill.getIsReturned() == 0) {
				writtenBackForward(bill, writterEntry, checkEntryList1, FinReceivableCheckEntry.class);
			}
		}

		//后置状态
		for(StkIo bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void purInvoiceWriteBack(List<FinPurInvoiceEntry> invoiceEntryList, boolean reverse) {
		Map<String, StkIo> billMap = new HashMap<>();
		Map<String, StkIoEntry> entryMap = new HashMap<>();
		List<FinPurInvoiceEntry> invoiceEntryList1 = new ArrayList<>();
		for(FinPurInvoiceEntry writter: invoiceEntryList) {
			String srcBillType = writter.getSrcBillType();
			if(!StringUtils.isEmpty(srcBillType) && srcBillType.startsWith("StkIo")) {
				Pair<StkIo, StkIoEntry> pair =
						this.invoiceWriteBack(writter.getSrcBillId(), writter.getSrcEntryId(), writter.getSrcNo(),
						writter.getUnitId(), writter.getQty(), writter.getAmt(), reverse, billMap, entryMap);

				//向前转置：如果是退货，不回写订单（订单可能已关闭、结算毛利润等）
				if (pair.getFirst().getIsReturned() == 0) {
					writtenBackForward(pair.getSecond(), writter, invoiceEntryList1, FinPurInvoiceEntry.class);
				}
			}
		}

		for(StkIoEntry entry: entryMap.values()) {
			this.entryMapper.updateById(entry);
		}
		for(StkIo bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void salInvoiceWriteBack(List<FinSalInvoiceEntry> invoiceEntryList, boolean reverse) {
		Map<String, StkIo> billMap = new HashMap<>();
		Map<String, StkIoEntry> entryMap = new HashMap<>();
		List<FinSalInvoiceEntry> invoiceEntryList1 = new ArrayList<>();
		for(FinSalInvoiceEntry writter: invoiceEntryList) {
			String srcBillType = writter.getSrcBillType();
			if(!StringUtils.isEmpty(srcBillType) && srcBillType.startsWith("StkIo")) {
				Pair<StkIo, StkIoEntry> pair =
						this.invoiceWriteBack(writter.getSrcBillId(), writter.getSrcEntryId(), writter.getSrcNo(),
						writter.getUnitId(), writter.getQty(), writter.getAmt(), reverse, billMap, entryMap);

				//向前转置：如果是退货，不回写订单（订单可能已关闭、结算毛利润等）
				if (pair.getFirst().getIsReturned() == 0) {
					writtenBackForward(pair.getSecond(), writter, invoiceEntryList1, FinSalInvoiceEntry.class);
				}
			}
		}

		for(StkIoEntry entry: entryMap.values()) {
			this.entryMapper.updateById(entry);
		}
		for(StkIo bill: billMap.values()) {
			this.baseMapper.updateById(bill);
			this.refreshExecuteStage(bill);
		}
	}

	private Pair<StkIo, StkIoEntry> invoiceWriteBack(String srcBillId, String srcEntryId, String srcNo,
													 String unitId, BigDecimal qty, BigDecimal amt, boolean reverse,
													 Map<String, StkIo> billMap, Map<String, StkIoEntry> entryMap) {
		//前置状态
		StkIo bill = billMap.get(srcBillId);
		if (bill == null) {
			bill = this.baseMapper.selectById(srcBillId);
			if (bill == null) {
				throw new JeecgBootException(srcNo.split(":")[0] + "：单据未找到，可能被其他用户删除了！");
			}

			if (bill.getIsEffective() == 0 || bill.getIsVoided() == 1) {
				throw new JeecgBootException(bill.getBillNo() + "：单据未生效或被作废，不能被回写！");
			}
			billMap.put(srcBillId, bill);
		}

		StkIoEntry entry = entryMap.get(srcEntryId);
		if (entry == null) {
			entry = this.entryMapper.selectById(srcEntryId);
			if (entry == null){
				throw new JeecgBootException(srcNo + "：出入库分录不存在！");
			}
			entryMap.put(srcEntryId, entry);
		}

		//数据处理
		if (!unitId.equals(entry.getUnitId())) {
			qty = BillUtils.convertUnit(qty, unitId, entry.getUnitId());
		}
		if (reverse){
			qty = qty.negate();
			amt = amt.negate();
		}

		entry.setInvoicedQty(entry.getInvoicedQty().add(qty));
		int i = entry.getInvoicedQty().compareTo(entry.getSettleQty());
		if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
			throw new JeecgBootException(srcNo + "：开票数量不能超出未开票数量！");
		}

		entry.setInvoicedAmt(entry.getInvoicedAmt().add(amt));
		i = entry.getInvoicedQty().compareTo(entry.getSettleQty());
		if (bill.getIsRubric() == 0 ? i > 0 : i < 0) {
			throw new JeecgBootException(srcNo + "：开票金额不能超出未开票金额！");
		}
		bill.setInvoicedAmt(bill.getInvoicedAmt().add(amt));

		return Pair.of(bill, entry);
	}

}
