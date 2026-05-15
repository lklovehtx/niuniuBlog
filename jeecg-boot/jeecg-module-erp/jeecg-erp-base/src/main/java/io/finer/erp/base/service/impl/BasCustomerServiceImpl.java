package io.finer.erp.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.finer.erp.base.entity.BasCustomer;
import io.finer.erp.base.mapper.BasCustomerMapper;
import io.finer.erp.base.service.IBasCustomerService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 客户
 * @Author: jeecg-boot
 * @Date:   2022-09-05
 * @Version: V1.0
 */
@Service
public class BasCustomerServiceImpl extends ServiceImpl<BasCustomerMapper, BasCustomer> implements IBasCustomerService {
    //20251030 cfm add
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(BasCustomer basCustomer) {
        if (basCustomer.getCreditQuota() == null) {
            UpdateWrapper<BasCustomer> wrapper = new UpdateWrapper<>();
            wrapper.set("credit_quota", null).eq("id", basCustomer.getId());
            update(null, wrapper);
        }
        updateById(basCustomer);
    }
}
