package com.nbcio.modules.flowable.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.flowable.entity.SysCustomForm;
import com.nbcio.modules.flowable.entity.SysDeployForm;
import com.nbcio.modules.flowable.entity.SysForm;
import com.nbcio.modules.flowable.mapper.SysDeployFormMapper;
import com.nbcio.modules.flowable.service.ISysDeployFormService;

/**
 * @Description: 流程实例关联表单
 * @Author: nbacheng
 * @Date:   2022-04-11
 * @Version: V1.0
 */
@Service
public class SysDeployFormServiceImpl extends ServiceImpl<SysDeployFormMapper, SysDeployForm> implements ISysDeployFormService {

	@Autowired
    private SysDeployFormMapper sysDeployFormMapper;
	
	@Override
	public SysForm selectSysDeployFormByDeployId(String deployId) {
		// TODO Auto-generated method stub
		return sysDeployFormMapper.selectSysDeployFormByDeployId(deployId);
	}
	
	@Override
	public SysCustomForm selectSysCustomFormByDeployId(String deployId) {
		// TODO Auto-generated method stub
		return sysDeployFormMapper.selectSysCustomFormByDeployId(deployId);
	}

	@Override
	public SysDeployForm selectSysDeployFormByFormId(String formId) {
		// TODO Auto-generated method stub
		return sysDeployFormMapper.selectSysDeployFormByFormId(formId);
	}

}
