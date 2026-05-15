package org.jeecg.modules.psi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.psi.entity.PsiIndependentDemand;
import org.jeecg.modules.psi.entity.PsiIndependentDemandItem;

import java.util.List;

public interface IPsiIndependentDemandService extends IService<PsiIndependentDemand> {

    IPage<PsiIndependentDemand> queryPageList(Page<PsiIndependentDemand> page, PsiIndependentDemand query);

    PsiIndependentDemand queryById(String id);

    List<PsiIndependentDemandItem> queryItemsByDemandId(String demandId);

    void saveDemandWithItems(PsiIndependentDemand demand);

    void updateDemandWithItems(PsiIndependentDemand demand);

    void deleteDemandWithItems(String id);

    void deleteBatchDemandWithItems(List<String> ids);

    String generateDemandNo();

    void saveItems(String demandId, List<PsiIndependentDemandItem> items);
}