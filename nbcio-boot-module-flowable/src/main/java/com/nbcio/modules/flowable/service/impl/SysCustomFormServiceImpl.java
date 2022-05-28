package com.nbcio.modules.flowable.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.flowable.domain.vo.CustomFormVo;
import com.nbcio.modules.flowable.entity.SysCustomForm;
import com.nbcio.modules.flowable.mapper.SysCustomFormMapper;
import com.nbcio.modules.flowable.service.ISysCustomFormService;

/**
 * @Description: 系统自定义表单表
 * @Author: nbacheng
 * @Date:   2022-04-23
 * @Version: V1.0
 */
@Service
public class SysCustomFormServiceImpl extends ServiceImpl<SysCustomFormMapper, SysCustomForm> implements ISysCustomFormService {

	@Autowired
	SysCustomFormMapper sysCustomFormMapper;
	
	@Override
	public SysCustomForm selectSysCustomFormById(String formId) {
		// TODO Auto-generated method stub
		return sysCustomFormMapper.selectSysCustomFormById(formId);
	}

	@Override
	public SysCustomForm selectSysCustomFormByServiceName(String serviceName) {
		// TODO Auto-generated method stub
		return sysCustomFormMapper.selectSysCustomFormByServiceName(serviceName);
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCustom(CustomFormVo customFormVo) {
		sysCustomFormMapper.updateCustom(customFormVo);
	}
}
