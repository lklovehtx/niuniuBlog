package org.jeecg.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysOption;

/**
 * @Description: 系统配置
 * @Author: cfm
 * @Date: 2025-12-11
 * @Version: V1.0
 */
public interface ISysOptionService extends IService<SysOption> {
    JSONObject getOptions();
}
