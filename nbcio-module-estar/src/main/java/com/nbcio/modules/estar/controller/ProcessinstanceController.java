package com.nbcio.modules.estar.controller;

import com.alibaba.fastjson.JSON;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.request.OapiProcessinstanceGetRequest;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse.ProcessInstanceTopVo;
import org.jeecg.common.aspect.annotation.AutoLog;

import com.nbcio.modules.estar.config.Constant;
import com.nbcio.modules.estar.config.URLConstant;
import com.nbcio.modules.estar.model.ProcessInstanceInputVO;
import com.nbcio.modules.estar.service.IOaProcessinstanceService;
import com.nbcio.modules.estar.util.AccessTokenUtil;
import com.nbcio.modules.estar.util.LogFormatter;
import com.nbcio.modules.estar.util.ServiceResult;
import com.nbcio.modules.estar.util.ServiceResultCode;
import com.nbcio.modules.estar.util.LogFormatter.LogEvent;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业 E应用审批解决方案示例代码
 * 实现了审批的基础功能
 */
@Api(tags = "钉钉流程审批接口")
@RestController
public class ProcessinstanceController {
	private static final Logger bizLogger = LoggerFactory.getLogger(ProcessinstanceController.class);

	@Autowired
	private IOaProcessinstanceService oaProcessinstanceService;
	
	/**
	 * 欢迎页面
	 */
	@RequestMapping(value = "/Processinstance/welcome", method = RequestMethod.GET)
	public String welcome() {
		return "welcome";
	}


	/**
	 * 发起审批
	 */
	@RequestMapping(value = "/processinstance/start", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult<String> startProcessInstance(@RequestBody ProcessInstanceInputVO processInstance) {
		try {
			DefaultDingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_PROCESSINSTANCE_START);
			OapiProcessinstanceCreateRequest request = new OapiProcessinstanceCreateRequest();
			request.setProcessCode(Constant.PROCESS_CODE);

			request.setFormComponentValues(processInstance.generateForms());

			/**
			 * 如果想复用审批固定流程，使用或签会签的话，可以不传审批人，具体请参考文档： https://open-doc.dingtalk.com/microapp/serverapi2/ebkwx8
			 * 本次quickstart，演示不传审批人的场景
			 */
			request.setApprovers(processInstance.getOriginatorUserId());
			request.setOriginatorUserId(processInstance.getOriginatorUserId());
			request.setDeptId(processInstance.getDeptId());
			request.setCcList(processInstance.getOriginatorUserId());
			request.setCcPosition("FINISH");

			OapiProcessinstanceCreateResponse response = client.execute(request, AccessTokenUtil.getToken());

			if (response.getErrcode().longValue() != 0) {
				return ServiceResult.failure(String.valueOf(response.getErrorCode()), response.getErrmsg());
			}
			return ServiceResult.success(response.getProcessInstanceId());

		} catch (Exception e) {
			String errLog = LogFormatter.getKVLogData(LogEvent.END,
				LogFormatter.KeyValue.getNew("processInstance", JSON.toJSONString(processInstance)));
			bizLogger.info(errLog,e);
			return ServiceResult.failure(ServiceResultCode.SYS_ERROR.getErrCode(),ServiceResultCode.SYS_ERROR.getErrMsg());
		}
	}

	/**
	 * 根据审批实例id获取审批详情
	 * @param instanceId
	 * @return
	 */
	@AutoLog(value = "获取钉钉流程审批详情")
	@ApiOperation(value = "获取钉钉流程审批详情", notes = "获取钉钉流程审批详情")
	@RequestMapping(value = "/processinstance/get", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult getProcessinstanceById(@RequestParam String instanceId) {
		try {
			DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_PROCESSINSTANCE_GET);
			OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
			request.setProcessInstanceId(instanceId);
			OapiProcessinstanceGetResponse response = client.execute(request, AccessTokenUtil.getToken());
			if (response.getErrcode().longValue() != 0) {
				return ServiceResult.failure(String.valueOf(response.getErrorCode()), response.getErrmsg());
			}
			return ServiceResult.success(response.getProcessInstance());
		} catch (Exception e) {
			String errLog = LogFormatter.getKVLogData(LogEvent.END,
				LogFormatter.KeyValue.getNew("instanceId", instanceId));
			bizLogger.info(errLog,e);
			return ServiceResult.failure(ServiceResultCode.SYS_ERROR.getErrCode(),ServiceResultCode.SYS_ERROR.getErrMsg());
		}
	}
	
	/**
	 * 根据审批实例id获取审批格式化过的详情  add by nbacheng
	 * @param instanceId
	 * @return
	 */
	@AutoLog(value = "获取钉钉流程格式化过的详情")
	@ApiOperation(value = "获取钉钉流程格式化过的详情", notes = "获取钉钉流程格式化过的详情")
	@RequestMapping(value = "/processinstance/getdetail", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult getDetailProcesseById(@RequestParam String instanceId) {
		try {
			DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_PROCESSINSTANCE_GET);
			OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
			request.setProcessInstanceId(instanceId);
			OapiProcessinstanceGetResponse response = client.execute(request, AccessTokenUtil.getToken());
			if (response.getErrcode().longValue() != 0) {
				return ServiceResult.failure(String.valueOf(response.getErrorCode()), response.getErrmsg());
			}
			//若流程完成更新薪资流程表信息
			oaProcessinstanceService.UpdateProcessStatus(instanceId, response.getProcessInstance());
			//替换获取的username替换成realname
			ProcessInstanceTopVo processInstanceTopVo = oaProcessinstanceService.getProcesseDetailByUserIds(response.getProcessInstance()) ;			
			return ServiceResult.success(processInstanceTopVo);
		} catch (Exception e) {
			String errLog = LogFormatter.getKVLogData(LogEvent.END,
				LogFormatter.KeyValue.getNew("instanceId", instanceId));
			bizLogger.info(errLog,e);
			return ServiceResult.failure(ServiceResultCode.SYS_ERROR.getErrCode(),ServiceResultCode.SYS_ERROR.getErrMsg());
		}
	}
	
}


