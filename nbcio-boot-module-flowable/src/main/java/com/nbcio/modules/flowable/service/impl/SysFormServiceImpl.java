package com.nbcio.modules.flowable.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.flowable.entity.SysForm;
import com.nbcio.modules.flowable.mapper.SysFormMapper;
import com.nbcio.modules.flowable.service.ISysFormService;

/**
 * @Description: 系统流程表单
 * @Author: nbacheng
 * @Date:   2022-04-07
 * @Version: V1.0
 */
@Service
public class SysFormServiceImpl extends ServiceImpl<SysFormMapper, SysForm> implements ISysFormService {
	
	@Autowired
	SysFormMapper sysFormMapper;
	
	/**
     * 查询流程表单
     * 
     * @param formId 流程表单ID
     * @return 流程表单
     */
    @Override
    public SysForm selectSysFormById(String formId)
    {
        return sysFormMapper.selectSysFormById(formId);
    }
}
