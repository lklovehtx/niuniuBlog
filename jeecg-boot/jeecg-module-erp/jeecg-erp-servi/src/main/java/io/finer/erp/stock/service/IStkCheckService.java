package io.finer.erp.stock.service;

import io.finer.erp.common.service.IBillWithEntryService;
import io.finer.erp.stock.entity.StkCheck;
import io.finer.erp.stock.entity.StkCheckEntry;
import io.finer.erp.stock.entity.StkCheckSingle;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 盘点卡
 * @Author:
 * @Date:
 * @Version: V1.0
 */
public interface IStkCheckService extends IBillWithEntryService<StkCheck, StkCheckEntry> {

    //begin-20231031 cfm add
    @Transactional(rollbackFor = Exception.class)
    void saveAdd(StkCheck bill, List<StkCheckEntry> entryList, List<StkCheckSingle> singleList) throws Exception;

    @Transactional(rollbackFor = Exception.class)
    void submitAdd(StkCheck bill, List<StkCheckEntry> entryList, List<StkCheckSingle> singleList) throws Exception;

    @Transactional(rollbackFor = Exception.class)
    void saveEdit(StkCheck bill, List<StkCheckEntry> entryList, List<StkCheckSingle> singleList) throws Exception;

    @Transactional(rollbackFor = Exception.class)
    void submitEdit(StkCheck bill, List<StkCheckEntry> entryList, List<StkCheckSingle> singleList) throws Exception;
    //end-20231031 cfm add

    //20231210 cfm add
    @Transactional(rollbackFor = Exception.class)
    void saveAddBatch(Map<StkCheck,List<StkCheckEntry>> billMap, Map<StkCheck, List<StkCheckSingle>> billMap2) throws Exception;

}
