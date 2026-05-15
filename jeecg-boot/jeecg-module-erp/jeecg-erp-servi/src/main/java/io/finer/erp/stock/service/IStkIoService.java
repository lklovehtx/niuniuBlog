package io.finer.erp.stock.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.finance.entity.FinPayableCheckEntry;
import io.finer.erp.finance.entity.FinPurInvoiceEntry;
import io.finer.erp.finance.entity.FinReceivableCheckEntry;
import io.finer.erp.finance.entity.FinSalInvoiceEntry;
import io.finer.erp.stock.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 出入库单
 * @Author:
 * @Date:
 * @Version:
 */
public interface IStkIoService extends IBillWithEntryService<StkIo, StkIoEntry> {

	//begin-20230916 cfm add
	@Transactional(rollbackFor = Exception.class)
	void saveAdd(StkIo bill, List<StkIoEntry> entryList, List<StkIoSingle> singleList) throws Exception;

	@Transactional(rollbackFor = Exception.class)
	void submitAdd(StkIo bill, List<StkIoEntry> entryList, List<StkIoSingle> singleList) throws Exception;

	@Transactional(rollbackFor = Exception.class)
	void saveEdit(StkIo bill, List<StkIoEntry> entryList, List<StkIoSingle> singleList) throws Exception;

	@Transactional(rollbackFor = Exception.class)
	void submitEdit(StkIo bill, List<StkIoEntry> entryList, List<StkIoSingle> singleList) throws Exception;
	//end-20230916 cfm add

	//20231210 cfm add
	@Transactional(rollbackFor = Exception.class)
	void saveAddBatch(Map<StkIo,List<StkIoEntry>> billMap, Map<StkIo, List<StkIoSingle>> billMap2) throws Exception;

	/**
	 * 创建盘盈入库单
	 * @param stkCheck
	 * @param stkCheckEntryList
	 */
	@Transactional(rollbackFor = Exception.class)
    //20231101 cfm modi: 增加参数 stkCheckSingleList
	void createInBill(StkCheck stkCheck, List<StkCheckEntry> stkCheckEntryList, List<StkCheckSingle> stkCheckSingleList) throws Exception;

	/**
	 * 创建盘亏出库单
	 * @param stkCheck
	 * @param stkCheckEntryList
	 */
	@Transactional(rollbackFor = Exception.class)
    //20231101 cfm modi: 增加参数 stkCheckSingleList
	void createOutBill(StkCheck stkCheck, List<StkCheckEntry> stkCheckEntryList, List<StkCheckSingle> stkCheckSingleList) throws Exception;

	/**
	 * 后置回写更新——已结算金额
	 * @param checkEntryList
	 * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
	 */
	@Transactional(rollbackFor = Exception.class)
	void payableCheckWriteBack(List<FinPayableCheckEntry> checkEntryList, boolean reverse);

	/**
	 * 后置回写更新——已结算金额
	 * @param checkEntryList
	 * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
	 */
	@Transactional(rollbackFor = Exception.class)
	void receivableCheckWriteBack(List<FinReceivableCheckEntry> checkEntryList, boolean reverse);

	/**
	 * 后置回写更新——已开票xx
	 * @param invoiceEntryList
	 * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
	 */
	@Transactional(rollbackFor = Exception.class)
	void purInvoiceWriteBack(List<FinPurInvoiceEntry> invoiceEntryList, boolean reverse);

	/**
	 * 后置回写更新——已开票xx
	 * @param invoiceEntryList
	 * @param reverse: false-后置单据生效后回写，true-后置单据作废前反回写
	 */
	@Transactional(rollbackFor = Exception.class)
	void salInvoiceWriteBack(List<FinSalInvoiceEntry> invoiceEntryList, boolean reverse);

}
