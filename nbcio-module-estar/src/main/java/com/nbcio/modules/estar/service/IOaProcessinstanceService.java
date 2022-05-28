package com.nbcio.modules.estar.service;

/**
 * 业务层需实现的接口定义,在system模块里实现
 *@author nbacheng
 *@date 2022/04/02
 *@version 1.0
 */
import com.dingtalk.api.response.OapiProcessinstanceGetResponse.ProcessInstanceTopVo;
import com.nbcio.modules.estar.vo.ProcessUpdateVo;

public interface IOaProcessinstanceService {
	ProcessInstanceTopVo getProcesseDetailByUserIds(ProcessInstanceTopVo processInstanceTopVo);

	void UpdateProcessStatus(String instanceId,ProcessInstanceTopVo processInstanceTopVo);

}
