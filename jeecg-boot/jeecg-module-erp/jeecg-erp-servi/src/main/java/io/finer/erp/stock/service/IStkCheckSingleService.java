package io.finer.erp.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.finer.erp.stock.entity.StkCheckSingle;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 个体
 * @Author:
 * @Date: 2023-10-30
 * @Version: V1.0
 */
public interface IStkCheckSingleService extends IService<StkCheckSingle> {
    List<StkCheckSingle> selectByMainId(String mainId);

    @Transactional(rollbackFor = Exception.class)
    void deleteByMainId(String mainId);
}
