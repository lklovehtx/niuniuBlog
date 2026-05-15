package io.finer.erp.base.service;

import io.finer.erp.base.entity.BasCustomer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 客户
 * @Author: jeecg-boot
 * @Date:   2022-09-05
 * @Version: V1.0
 */
public interface IBasCustomerService extends IService<BasCustomer> {
    /**
     *  编辑
     *  注意：如果属性creditQuota为null，则数据库中将被修改为null；其他属性为null，不修改。
     *
     * @param basCustomer
     */
    void edit(BasCustomer basCustomer); //20251030 cfm add
}
