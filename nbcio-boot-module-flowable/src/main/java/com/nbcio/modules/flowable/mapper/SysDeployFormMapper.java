package com.nbcio.modules.flowable.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.flowable.entity.SysCustomForm;
import com.nbcio.modules.flowable.entity.SysDeployForm;
import com.nbcio.modules.flowable.entity.SysForm;

/**
 * @Description: 流程实例关联表单
 * @Author: nbacheng
 * @Date:   2022-04-11
 * @Version: V1.0
 */
public interface SysDeployFormMapper extends BaseMapper<SysDeployForm> {

	/**
     * 查询关联表内容
     * @param formId
     * @return
     */
	SysDeployForm selectSysDeployFormByFormId(String formId);
	/**
     * 查询流程挂着的表单
     * @param deployId
     * @return
     */
    SysForm selectSysDeployFormByDeployId(String deployId);
    
    /**
     * 查询流程挂着的自定义表单
     * @param deployId
     * @return
     */
    SysCustomForm selectSysCustomFormByDeployId(String deployId);

}
