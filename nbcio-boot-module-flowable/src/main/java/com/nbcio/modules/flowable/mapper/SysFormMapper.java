package com.nbcio.modules.flowable.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.flowable.entity.SysForm;

/**
 * @Description: 系统流程表单
 * @Author: nbacheng
 * @Date:   2022-04-07
 * @Version: V1.0
 */
public interface SysFormMapper extends BaseMapper<SysForm> {

	SysForm selectSysFormById(String formId);

}
