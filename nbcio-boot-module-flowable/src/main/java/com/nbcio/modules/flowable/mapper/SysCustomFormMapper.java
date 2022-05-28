package com.nbcio.modules.flowable.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.flowable.domain.vo.CustomFormVo;
import com.nbcio.modules.flowable.entity.SysCustomForm;

/**
 * @Description: 系统自定义表单表
 * @Author: nbacheng
 * @Date:   2022-04-23
 * @Version: V1.0
 */
public interface SysCustomFormMapper extends BaseMapper<SysCustomForm> {
	SysCustomForm selectSysCustomFormById(String formId);
	SysCustomForm selectSysCustomFormByServiceName(String serviceName);
	void updateCustom(@Param("customFormVo") CustomFormVo customFormVo);
}
