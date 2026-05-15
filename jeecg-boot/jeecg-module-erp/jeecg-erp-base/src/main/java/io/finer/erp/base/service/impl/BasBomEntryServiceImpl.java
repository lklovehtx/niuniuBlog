package io.finer.erp.base.service.impl;

import io.finer.erp.base.entity.BasBomEntry;
import io.finer.erp.base.mapper.BasBomEntryMapper;
import io.finer.erp.base.service.IBasBomEntryService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: BOM明细
 * @Author: jeecg-boot
 * @Date:   2023-12-22
 * @Version: V1.0
 */
@Service
public class BasBomEntryServiceImpl extends ServiceImpl<BasBomEntryMapper, BasBomEntry> implements IBasBomEntryService {
	
	@Autowired
	private BasBomEntryMapper basBomEntryMapper;
	
	@Override
	public List<BasBomEntry> selectByMainId(String mainId) {
		return basBomEntryMapper.selectByMainId(mainId);
	}
}
