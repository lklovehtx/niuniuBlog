package io.finer.erp.base.service;

import io.finer.erp.base.entity.BasMaterial;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 物料
 * @Author: jeecg-boot
 * @Date:   2020-05-29
 * @Version: V1.0
 */
public interface IBasMaterialService extends IService<BasMaterial> {
    /**
     *  编辑
     *  注意：如果属性bas_material为null，则数据库中将被修改为null；其他属性为null，不修改。
     *
     * @param basMaterial
     */
    void edit(BasMaterial basMaterial); //20251030 cfm add

}
