package org.jeecg.modules.psi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.psi.entity.PsiIndependentDemand;
import org.jeecg.modules.psi.entity.PsiIndependentDemandItem;

import java.util.List;

public interface PsiIndependentDemandMapper extends BaseMapper<PsiIndependentDemand> {

    IPage<PsiIndependentDemand> queryPageList(Page<PsiIndependentDemand> page, @Param("query") PsiIndependentDemand query);

    PsiIndependentDemand queryById(@Param("id") String id);

    List<PsiIndependentDemandItem> queryItemsByDemandId(@Param("demandId") String demandId);

    void deleteItemsByDemandId(@Param("demandId") String demandId);

    void insertItem(PsiIndependentDemandItem item);

    void updateItem(PsiIndependentDemandItem item);

    List<PsiIndependentDemand> queryListByDemandNo(@Param("demandNo") String demandNo);
}