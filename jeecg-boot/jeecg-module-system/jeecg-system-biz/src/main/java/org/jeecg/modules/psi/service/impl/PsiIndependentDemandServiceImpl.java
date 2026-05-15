package org.jeecg.modules.psi.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.psi.entity.PsiIndependentDemand;
import org.jeecg.modules.psi.entity.PsiIndependentDemandItem;
import org.jeecg.modules.psi.mapper.PsiIndependentDemandMapper;
import org.jeecg.modules.psi.service.IPsiIndependentDemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service("psiIndependentDemandService")
@Slf4j
public class PsiIndependentDemandServiceImpl extends ServiceImpl<PsiIndependentDemandMapper, PsiIndependentDemand> implements IPsiIndependentDemandService {

    @Autowired
    private PsiIndependentDemandMapper psiIndependentDemandMapper;

    @Override
    public IPage<PsiIndependentDemand> queryPageList(Page<PsiIndependentDemand> page, PsiIndependentDemand query) {
        return psiIndependentDemandMapper.queryPageList(page, query);
    }

    @Override
    public PsiIndependentDemand queryById(String id) {
        PsiIndependentDemand demand = psiIndependentDemandMapper.queryById(id);
        if (demand != null) {
            List<PsiIndependentDemandItem> items = psiIndependentDemandMapper.queryItemsByDemandId(id);
            demand.setItems(items);
        }
        return demand;
    }

    @Override
    public List<PsiIndependentDemandItem> queryItemsByDemandId(String demandId) {
        return psiIndependentDemandMapper.queryItemsByDemandId(demandId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDemandWithItems(PsiIndependentDemand demand) {
        if (demand.getDemandNo() == null || demand.getDemandNo().isEmpty()) {
            demand.setDemandNo(generateDemandNo());
        }
        demand.setDelFlag("0");
        demand.setDemandStatus(demand.getDemandStatus() != null ? demand.getDemandStatus() : "0");
        
        this.save(demand);

        if (demand.getItems() != null && !demand.getItems().isEmpty()) {
            int lineNo = 1;
            for (PsiIndependentDemandItem item : demand.getItems()) {
                item.setId(UUID.randomUUID().toString());
                item.setDemandId(demand.getId());
                item.setDemandNo(demand.getDemandNo());
                item.setLineNo(lineNo++);
                item.setDelFlag("0");
                item.setDemandStatus(demand.getDemandStatus());
                item.setCreateBy(demand.getCreateBy());
                item.setCreateTime(demand.getCreateTime());
                psiIndependentDemandMapper.insertItem(item);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDemandWithItems(PsiIndependentDemand demand) {
        this.updateById(demand);

        psiIndependentDemandMapper.deleteItemsByDemandId(demand.getId());

        if (demand.getItems() != null && !demand.getItems().isEmpty()) {
            int lineNo = 1;
            for (PsiIndependentDemandItem item : demand.getItems()) {
                item.setId(UUID.randomUUID().toString());
                item.setDemandId(demand.getId());
                item.setDemandNo(demand.getDemandNo());
                item.setLineNo(lineNo++);
                item.setDelFlag("0");
                item.setDemandStatus(demand.getDemandStatus());
                item.setUpdateBy(demand.getUpdateBy());
                item.setUpdateTime(new Date());
                psiIndependentDemandMapper.insertItem(item);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDemandWithItems(String id) {
        PsiIndependentDemand demand = this.getById(id);
        if (demand != null) {
            demand.setDelFlag("1");
            this.updateById(demand);
            psiIndependentDemandMapper.deleteItemsByDemandId(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchDemandWithItems(List<String> ids) {
        for (String id : ids) {
            deleteDemandWithItems(id);
        }
    }

    @Override
    public String generateDemandNo() {
        String prefix = "ID";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(4);
        return prefix + timestamp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveItems(String demandId, List<PsiIndependentDemandItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        PsiIndependentDemand demand = this.getById(demandId);
        if (demand == null) {
            return;
        }

        int lineNo = 1;
        for (PsiIndependentDemandItem item : items) {
            item.setId(UUID.randomUUID().toString());
            item.setDemandId(demandId);
            item.setDemandNo(demand.getDemandNo());
            item.setLineNo(lineNo++);
            item.setDelFlag("0");
            item.setDemandStatus(demand.getDemandStatus());
            psiIndependentDemandMapper.insertItem(item);
        }
    }
}