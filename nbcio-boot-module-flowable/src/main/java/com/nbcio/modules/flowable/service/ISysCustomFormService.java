package com.nbcio.modules.flowable.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.flowable.domain.vo.CustomFormVo;
import com.nbcio.modules.flowable.entity.SysCustomForm;

/**
 * @Description: 系统自定义表单表
 * @Author: nbacheng
 * @Date:   2022-04-23
 * @Version: V1.0
 */
public interface ISysCustomFormService extends IService<SysCustomForm> {

	SysCustomForm selectSysCustomFormById(String formId);

	SysCustomForm selectSysCustomFormByServiceName(String serviceName);

	void updateCustom(CustomFormVo customFormVo);

}
