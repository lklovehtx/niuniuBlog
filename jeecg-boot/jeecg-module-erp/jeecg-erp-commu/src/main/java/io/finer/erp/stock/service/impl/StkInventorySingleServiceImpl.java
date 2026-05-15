package io.finer.erp.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.finer.erp.base.entity.BasMaterial;
import io.finer.erp.base.service.IBasMaterialService;
import io.finer.erp.base.service.IBasUnitService;
import io.finer.erp.stock.entity.StkInventorySingle;
import io.finer.erp.stock.entity.StkIo;
import io.finer.erp.stock.entity.StkIoEntry;
import io.finer.erp.stock.entity.StkIoSingle;
import io.finer.erp.stock.mapper.StkInventorySingleMapper;
import io.finer.erp.stock.service.IStkInventorySingleService;
import io.finer.erp.stock.service.IStkIoSingleService;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Description: 个体即时库存
 * @Author:
 * @Date: 2023-09-10
 * @Version: V1.0mybatis
 */
@Service
public class StkInventorySingleServiceImpl
        extends ServiceImpl<StkInventorySingleMapper, StkInventorySingle>
        implements IStkInventorySingleService {

    @Autowired
    IBasMaterialService basMaterialService;
    @Autowired
    IBasUnitService basUnitService;
    @Lazy
    @Autowired
    IStkIoSingleService stkIoSingleService;

    @Override
    public StkInventorySingle getSingle(String materialId, String sn) {
        StkInventorySingle ms = new StkInventorySingle();
        ms.setMaterialId(materialId);
        ms.setSn(sn);
        QueryWrapper<StkInventorySingle> queryWrapper = new QueryWrapper<StkInventorySingle>(ms);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSingle(StkIo stkIo, List<StkIoEntry> stkIoEntryList, List<StkIoSingle> stkIoSingleList, boolean reverse) {
        if (stkIoSingleList == null) {
            return;
        }

        //库存调拨：单独处理
        if (stkIo.getStockIoType().equals("301")) {
            if (!reverse) {
                moveStock0(stkIoSingleList);
            } else {
                moveStock1(stkIoSingleList);
            }
            return;
        }

        for (StkIoSingle ioSingle: stkIoSingleList) {
            if (!StringUtils.hasLength(ioSingle.getSn())) {
                continue;
            }

            String stockIoType = stkIo.getStockIoType();
            if (reverse) {
                //取负
                ioSingle.setQty(ioSingle.getQty().negate());
                ioSingle.setCost(ioSingle.getCost().negate());
            }

            if (stockIoType.equals("801")) { //成本调整
                this.changeCost(ioSingle);
            } else if (ioSingle.getStockIoDirection().equals("1")) { //入
                this.inStock(stkIo, ioSingle);
            } else if (ioSingle.getStockIoDirection().equals("2")) { //出
                this.outStock(ioSingle);
            } else {
                throwException(ioSingle, "出入库方向非法！");
            }

            if (reverse) {
                //恢复
                ioSingle.setQty(ioSingle.getQty().negate());
                ioSingle.setCost(ioSingle.getCost().negate());
            }
        }
    }

    private void inStock(StkIo stkIo, StkIoSingle ioSingle) {
        StkInventorySingle is_new = new StkInventorySingle();
        is_new.setMaterialId(ioSingle.getMaterialId());
        is_new.setSn(ioSingle.getSn());

        QueryWrapper<StkInventorySingle> queryWrapper = new QueryWrapper<StkInventorySingle>(is_new);
        StkInventorySingle is = this.baseMapper.selectOne(queryWrapper);
        if (is == null) {
            BasMaterial material = basMaterialService.getById(ioSingle.getMaterialId());
            if (material == null) {
                throwException(ioSingle, "物料在物料表中不存在！");
            }

            if (StringUtils.hasLength(stkIo.getSupplierId())) {
                is_new.setSupplierId(stkIo.getSupplierId());
            }
            is_new.setWarehouseId(ioSingle.getWarehouseId());
            is_new.setBatchNo(ioSingle.getBatchNo());
            is_new.setUnitId(material.getUnitId());
            BigDecimal qty = basUnitService.convert(ioSingle.getQty(), ioSingle.getUnitId(), is_new.getUnitId());
            is_new.setQty(qty);
            is_new.setCost(ioSingle.getCost());
            this.baseMapper.insert(is_new);
        } else {
             if (is.getQty().compareTo(BigDecimal.ZERO) == 0) {
                is.setWarehouseId(ioSingle.getWarehouseId());
                is.setBatchNo(ioSingle.getBatchNo());
            } else if (!ioSingle.getWarehouseId().equals(is.getWarehouseId()) || !ioSingle.getBatchNo().equals(is.getBatchNo())) {
                throwException(ioSingle, "该个体在库中的仓库、批次与本次出入库的不一致！");
            }

            if (StringUtils.hasLength(stkIo.getSupplierId())) {
                is.setSupplierId(stkIo.getSupplierId());
            }
            BigDecimal qty = basUnitService.convert(ioSingle.getQty(), ioSingle.getUnitId(), is.getUnitId());
            is.setQty(is.getQty().add(qty));
            is.setCost(is.getCost().add(ioSingle.getCost()));
            if (is.getQty().compareTo(BigDecimal.ZERO) < 0) {
                throwException(ioSingle, "该个体库存数量不足！");
            }
            this.baseMapper.updateById(is);
        }
    }

    private void outStock(StkIoSingle ioSingle) {
        StkInventorySingle is = new StkInventorySingle();
        is.setMaterialId(ioSingle.getMaterialId());
        is.setSn(ioSingle.getSn());
        QueryWrapper<StkInventorySingle> queryWrapper = new QueryWrapper<StkInventorySingle>(is);
        is = this.baseMapper.selectOne(queryWrapper);
        if (is == null) {
            throwException(ioSingle, "该个体不在仓库中！"); //【前提】个体即时库存：数量、成本为0的也不能删除，否则不能红出（销售退货入库）
        }

        //20240926 cfm modi for【优化】销售退货入库(红出)SN：由只能退回到原库原批次，改为可以退回到其他库其他批次。
        if (is.getQty().compareTo(BigDecimal.ZERO) == 0) {
            is.setWarehouseId(ioSingle.getWarehouseId());
            is.setBatchNo(ioSingle.getBatchNo());
        } else
        if (!ioSingle.getWarehouseId().equals(is.getWarehouseId()) || !ioSingle.getBatchNo().equals(is.getBatchNo())) {
            throwException(ioSingle, "该个体在库中的仓库、批次与本次出入库的不一致！");
        }

        BigDecimal qty = basUnitService.convert(ioSingle.getQty(), ioSingle.getUnitId(), is.getUnitId());
        is.setQty(is.getQty().subtract(qty));
        is.setCost(is.getCost().subtract(ioSingle.getCost()));
        if (is.getQty().compareTo(BigDecimal.ZERO) < 0) {
            throwException(ioSingle, "该个体库存数量不足！");
        }
        this.baseMapper.updateById(is);
    }

    private void changeCost(StkIoSingle ioSingle) {
        StkInventorySingle is = new StkInventorySingle();
        is.setMaterialId(ioSingle.getMaterialId());
        is.setSn(ioSingle.getSn());
        QueryWrapper<StkInventorySingle> queryWrapper = new QueryWrapper<StkInventorySingle>(is);
        is = this.baseMapper.selectOne(queryWrapper);
        if (is == null || is.getQty().compareTo(BigDecimal.ZERO) <= 0) {
            throwException(ioSingle, "该个体不在仓库中！");
        }
        if (!ioSingle.getWarehouseId().equals(is.getWarehouseId()) || !ioSingle.getBatchNo().equals(is.getBatchNo())) {
            throwException(ioSingle, "该个体在库中的仓库、批次与本次出入库的不一致！");
        }

        BigDecimal cost = is.getCost().add(ioSingle.getCost());
        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            throwException(ioSingle, "个体调整后成本不能为负！");
        }
        is.setCost(cost);
        this.baseMapper.updateById(is);
    }

    //库存调拨（正向）
    private void moveStock0(List<StkIoSingle> ioSingleList) {
        //对于库存调拨（正常）明细进行排序：调出在前，调入在后
        Collections.sort(ioSingleList, new Comparator<StkIoSingle>() {
            @Override
            public int compare(StkIoSingle arg0, StkIoSingle arg1) {
                return arg1.getStockIoDirection().compareTo(arg0.getStockIoDirection()); //降序：2-调出，1-调入
            }
        });

        for (StkIoSingle ioSingle : ioSingleList) {
            StkInventorySingle is = new StkInventorySingle();
            is.setMaterialId(ioSingle.getMaterialId());
            is.setSn(ioSingle.getSn());
            QueryWrapper<StkInventorySingle> queryWrapper = new QueryWrapper<StkInventorySingle>(is);
            is = this.baseMapper.selectOne(queryWrapper);
            if (is == null || is.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                throwException(ioSingle, "该个体不在仓库中！");
            }

            if (ioSingle.getStockIoDirection().equals("2")) { //出
                if (!is.getWarehouseId().equals(ioSingle.getWarehouseId()) || !is.getBatchNo().equals(ioSingle.getBatchNo())) {
                    throwException(ioSingle, "该个体不在要调出的仓库、批次中！");
                }
            } else { //入
                is.setWarehouseId(ioSingle.getWarehouseId());
                is.setBatchNo(ioSingle.getBatchNo());
                this.baseMapper.updateById(is);
            }
        }
    }

    //库存调拨（作废）
    private void moveStock1(List<StkIoSingle> ioSingleList) {
        //对于库存调拨（作废）明细进行排序：调入在前，调出在后
        Collections.sort(ioSingleList, new Comparator<StkIoSingle>() {
            @Override
            public int compare(StkIoSingle arg0, StkIoSingle arg1) {
                return arg0.getStockIoDirection().compareTo(arg1.getStockIoDirection()); //升序：1-调入， 2-调出
            }
        });

        for (StkIoSingle ioSingle : ioSingleList) {
            StkInventorySingle is = new StkInventorySingle();
            is.setMaterialId(ioSingle.getMaterialId());
            is.setSn(ioSingle.getSn());
            QueryWrapper<StkInventorySingle> queryWrapper = new QueryWrapper<StkInventorySingle>(is);
            is = this.baseMapper.selectOne(queryWrapper);
            if (is == null || is.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                throwException(ioSingle, "该个体不在仓库中！");
            }

            if (ioSingle.getStockIoDirection().equals("1")) { //入
                if (!is.getWarehouseId().equals(ioSingle.getWarehouseId()) || !is.getBatchNo().equals(ioSingle.getBatchNo())) {
                    throwException(ioSingle, "该个体已不在调入的仓库、批次中！");
                }
            } else { //出
                is.setWarehouseId(ioSingle.getWarehouseId());//恢复原仓库
                is.setBatchNo(ioSingle.getBatchNo());//恢复原批次
            }
            this.baseMapper.updateById(is);
        }
    }

    private void throwException(StkIoSingle ioSingle, String msg){
        BasMaterial m = basMaterialService.getById(ioSingle.getMaterialId());
        throw new JeecgBootException(String.format("【%s：%s】%s", m == null ? "（物料不存在）" : m.getName(), ioSingle.getSn(), msg));
    }

}
