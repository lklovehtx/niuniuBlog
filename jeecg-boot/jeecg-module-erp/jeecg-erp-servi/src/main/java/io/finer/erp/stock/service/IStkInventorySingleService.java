package io.finer.erp.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.entity.StkInventorySingle;
import io.finer.erp.stock.entity.StkIoSingle;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 个体即时库存
 * @Author:
 * @Date:   2023-09-10
 * @Version: V1.0
 */
public interface IStkInventorySingleService extends IService<StkInventorySingle> {
    StkInventorySingle getSingle(String materialId, String sn);

    @Transactional(rollbackFor = Exception.class)
    void updateSingle(StkIo stkIo, List<StkIoEntry> stkIoEntryList, List<StkIoSingle> stkIoSingleList, boolean reverse);
}
