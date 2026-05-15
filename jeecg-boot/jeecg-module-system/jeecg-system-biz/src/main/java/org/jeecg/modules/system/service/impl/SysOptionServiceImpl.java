package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.system.entity.SysOption;
import org.jeecg.modules.system.mapper.SysOptionMapper;
import org.jeecg.modules.system.service.ISysOptionService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 系统配置
 * @Author: cfm
 * @Date: 2025-12-11
 * @Version: V1.0
 */
@Service
@Component
public class SysOptionServiceImpl extends ServiceImpl<SysOptionMapper, SysOption> implements ISysOptionService {
    @Override
    public JSONObject getOptions() {
        JSONObject result = new JSONObject();
        List<SysOption> list = list();
        list.forEach(item -> result.put(item.getKey(), item.getValue()));
        return result;
    }
}
