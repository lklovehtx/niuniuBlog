package io.finer.erp.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.base.entity.BasMaterial;
import io.finer.erp.base.mapper.BasMaterialMapper;
import io.finer.erp.base.service.IBasMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 物料
 * @Author: jeecg-boot
 * @Date:   2020-05-29
 * @Version: V1.0
 */
@Service
public class BasMaterialServiceImpl extends ServiceImpl<BasMaterialMapper, BasMaterial> implements IBasMaterialService {
    //20251030 cfm add
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(BasMaterial basMaterial) {
        if (basMaterial.getSalePrice() == null) {
            UpdateWrapper<BasMaterial> wrapper = new UpdateWrapper<>();
            wrapper.set("sale_price", null).eq("id", basMaterial.getId());
            update(null, wrapper);
        }
        updateById(basMaterial);
    }
}
