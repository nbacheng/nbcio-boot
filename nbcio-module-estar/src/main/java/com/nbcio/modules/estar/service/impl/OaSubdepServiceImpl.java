package com.nbcio.modules.estar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.estar.entity.OaSubdep;
import com.nbcio.modules.estar.mapper.OaSubdepMapper;
import com.nbcio.modules.estar.service.IOaSubdepService;

/**
 * @Description: oa_subdep
 * @Author: nbacheng
 * @Date:   2022-02-24
 * @Version: V1.0
 */
@Service
public class OaSubdepServiceImpl extends ServiceImpl<OaSubdepMapper, OaSubdep> implements IOaSubdepService {

	@Autowired
	OaSubdepMapper oaSubdepMapper;
	
	@Override
	public List<OaSubdep> getSubDep(String upperno) {
		List<OaSubdep> oaSubdepList = oaSubdepMapper.getSubDep(upperno);
		return oaSubdepList;
	}
	
	@Override
	public List<OaSubdep> getDep(String depno) {
		List<OaSubdep> oaDepList = oaSubdepMapper.getDep(depno);
		return oaDepList;
	}
}
