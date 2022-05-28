package com.nbcio.modules.estar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.estar.entity.OaZbmhzb;
import com.nbcio.modules.estar.mapper.OaZbmhzbMapper;
import com.nbcio.modules.estar.service.IOaZbmhzbService;

/**
 * @Description: oa_zbmhzb
 * @Author: nbacheng
 * @Date:   2022-02-24
 * @Version: V1.0
 */
@Service
public class OaZbmhzbServiceImpl extends ServiceImpl<OaZbmhzbMapper, OaZbmhzb> implements IOaZbmhzbService {

	@Autowired
	OaZbmhzbMapper OaZbmhzbMapper;
	@Override
	public List<OaZbmhzb> getTotalSalaryBySubDep(String salarytime, String bmbh) {
		List<OaZbmhzb> oaZbmhzbList = OaZbmhzbMapper.getTotalSalaryBySubDep(salarytime, bmbh);
		return oaZbmhzbList;
	}

}
