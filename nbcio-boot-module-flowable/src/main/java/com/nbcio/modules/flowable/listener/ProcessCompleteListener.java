package com.nbcio.modules.flowable.listener;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nbcio.modules.flowable.apithird.entity.SysUser;
import com.nbcio.modules.flowable.apithird.service.IFlowThirdService;

/**
 * 全局监听-工作流完成消息提醒
 *
 * @author nbacheng
 */

//必须要用 AbstractFlowableEngineEventListener 用FlowableEventListener这个会出现问题，应该是已经完成了
@Component
public class ProcessCompleteListener extends AbstractFlowableEngineEventListener {

    @Resource
    private IFlowThirdService iFlowThirdService;
    
    @Autowired
    protected HistoryService historyService;
    
    @Resource
	protected RepositoryService repositoryService;

    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        System.out.println("进入流程结束监听器……");

        String procInsId = event.getProcessInstanceId();
        String taskId = event.getScopeId();
        HistoricProcessInstance hi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(procInsId)
                .singleResult();
        String startUserId = hi.getStartUserId();
		String businessKey =  hi.getBusinessKey();
		String deployId = hi.getDeploymentId();
        
        if (StringUtils.isNotEmpty(startUserId)) {
            // TODO:  发送提醒消息
        	SysUser loginUser = iFlowThirdService.getLoginUser();
        	String taskMessageUrl;
        	if(StringUtils.isNotBlank(businessKey)) {
    			taskMessageUrl = "<a href=" + iFlowThirdService.getBaseUrl() + "?procInsId=" + procInsId + "&deployId=" 
    				              + deployId + "&taskId=" + taskId + "&businessKey=" + businessKey
    				              + "&finished=true" + ">点击这个进行查看</a>" ;
    		}
    		else {
    			taskMessageUrl = "<a href=" + iFlowThirdService.getBaseUrl() + "?procInsId=" + procInsId + "&deployId=" 
    		              + deployId + "&taskId=" + taskId + "&businessKey" + "&finished=true" + ">点击这个进行查看</a>" ;
    		}
        	String msgContent = "流程任务结束通知" + taskMessageUrl; 
        	iFlowThirdService.sendSysAnnouncement(loginUser.getUsername(), startUserId, "流程任务结束通知", msgContent, "1");//setMsgCategory=1是通知
        }

        super.processCompleted(event);
    }

    @Override
    protected void taskCompleted(FlowableEngineEntityEvent event) {
        System.out.println("进入taskCompleted监听器……");
        super.taskCompleted(event);
    }

    @Override
    public void onEvent(FlowableEvent flowableEvent) {
        System.out.println("进入taskCompleted监听器--onEvent……");
        super.onEvent(flowableEvent);
    }
}