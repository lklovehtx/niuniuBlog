package io.finer.erp.base.service.impl;

import io.finer.erp.base.entity.BasBom;
import io.finer.erp.base.entity.BasBomEntry;
import io.finer.erp.base.mapper.BasBomEntryMapper;
import io.finer.erp.base.mapper.BasBomMapper;
import io.finer.erp.base.service.IBasBomService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: BOM
 * @Author: jeecg-boot
 * @Date:   2023-12-22
 * @Version: V1.0
 */
@Service
public class BasBomServiceImpl extends ServiceImpl<BasBomMapper, BasBom> implements IBasBomService {

	@Autowired
	private BasBomMapper basBomMapper;
	@Autowired
	private BasBomEntryMapper basBomEntryMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(BasBom basBom, List<BasBomEntry> basBomEntryList) {
		basBomMapper.insert(basBom);
		if(basBomEntryList!=null && basBomEntryList.size()>0) {
			for(BasBomEntry entity:basBomEntryList) {
				//外键设置
				entity.setMid(basBom.getId());
				basBomEntryMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(BasBom basBom,List<BasBomEntry> basBomEntryList) {
		basBomMapper.updateById(basBom);
		
		//1.先删除子表数据
		basBomEntryMapper.deleteByMainId(basBom.getId());
		
		//2.子表数据重新插入
		if(basBomEntryList!=null && basBomEntryList.size()>0) {
			for(BasBomEntry entity:basBomEntryList) {
				//外键设置
				entity.setMid(basBom.getId());
				basBomEntryMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		basBomEntryMapper.deleteByMainId(id);
		basBomMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			basBomEntryMapper.deleteByMainId(id.toString());
			basBomMapper.deleteById(id);
		}
	}
	
}
