package io.finer.erp.base.aspect;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.finer.erp.base.entity.BasMaterial;
import io.finer.erp.base.service.IBasMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 物料aop类
 * @Author:
 * @Date:
 * @Version: 1.0
 */
@Aspect
@Order(0)
@Component
@Slf4j
public class MaterialAspect {
    @Autowired
    private IBasMaterialService basMaterialService;
    @Autowired
    private ObjectMapper objectMapper;

    // 定义切点Pointcut
    @Pointcut("execution(public * io.finer..*.*Controller.query*ListByMainId*(..))" //20231211 cfm modi
            + "|| execution(public * io.finer..*.StkInventory*Controller.query*(..)) ")
    public void queryPointcut() {
    }

    @AfterReturning(returning = "result", pointcut = "queryPointcut()")
    public void doAfterReturning(Object result) {
        if (result instanceof Result) {
            Object obj = ((Result<?>) result).getResult();
            if (obj instanceof IPage) {
                List<Object> records = ((IPage) obj).getRecords();
                ((IPage) obj).setRecords(this.addFields(records));
            } else if (obj instanceof List<?>){
                List<?> records = (List<?>)obj;
                List<JSONObject> jsonObjs = this.addFields(records);
                ((Result) result).setResult(jsonObjs);
            } else if (obj != null){ //20231022 cfm modi: 增加【if (...)】
                List<?> records = Arrays.asList(obj);
                List<JSONObject> jsonObjs = this.addFields(records);
                ((Result) result).setResult(jsonObjs.get(0));
            }
        }
    }

    private List<JSONObject> addFields(List<?> records) {
        List<JSONObject> jsonObjs = new ArrayList<>();
        if (records == null || records.size() == 0) {
            return jsonObjs;
        }
        for (Object record : records) {
            JSONObject jsonObj;
            if (record instanceof JSONObject) {
                jsonObj = (JSONObject)record;
            } else {
                String jsonStr = "{}";
                try {
                    jsonStr = objectMapper.writeValueAsString(record);
                } catch (JsonProcessingException e) {
                    log.error("json解析失败" + e.getMessage(), e);
                }
                jsonObj = JSONObject.parseObject(jsonStr);
            }

            String materialId = jsonObj.getString("materialId");
            if (StringUtils.hasLength(materialId)
                    && (!jsonObj.containsKey("materialCode") || !jsonObj.containsKey("materialModel"))) {
                BasMaterial m = basMaterialService.getById(materialId);
                if (m != null) {
                    if(!jsonObj.containsKey("materialCode")) {
                        jsonObj.put("materialCode", m.getCode());
                    }
                    if (!jsonObj.containsKey("materialModel")) {
                        jsonObj.put("materialModel", m.getModel());
                    }
                    //20240513 cfm add
                    if (!jsonObj.containsKey("barcode")) {
                        jsonObj.put("barcode", m.getBarcode());
                    }
                }
            }
            jsonObjs.add(jsonObj);
        }
        return jsonObjs;
    }
}
