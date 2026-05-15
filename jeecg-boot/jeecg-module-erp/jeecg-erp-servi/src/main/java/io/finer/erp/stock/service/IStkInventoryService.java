package io.finer.erp.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.stock.entity.StkInventory;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.entity.StkIoSingle;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 库存
 * @Author: jeecg-boot
 * @Date:   2020-04-11
 * @Version: V1.0
 */
public interface IStkInventoryService extends IService<StkInventory> {
    StkInventory getInventory(String batchNo, String materialId, String warehouseId);

    //20230914 cfm modi
    @Transactional(rollbackFor = Exception.class)
    void updateInventory(StkIo stkIo, List<StkIoEntry> stkIoEntryList, List<StkIoSingle> stkIoSingleList, boolean reverse);
}
