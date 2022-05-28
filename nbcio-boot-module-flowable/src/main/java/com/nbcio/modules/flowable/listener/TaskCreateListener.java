package com.nbcio.modules.flowable.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

import com.nbcio.modules.flowable.apithird.entity.SysUser;
import com.nbcio.modules.flowable.apithird.service.IFlowThirdService;

import java.util.ArrayList;

import java.util.List;

import javax.annotation.Resource;

/**
 * 全局监听-工作流待办消息提醒
 *
 * @author nbacheng
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCreateListener implements FlowableEventListener {
	
    private final TaskService taskService;
    
    @Resource
    private IFlowThirdService iFlowThirdService;
    
    @Resource
	protected RepositoryService repositoryService;
	
	@Resource
    protected HistoryService historyService;
	   

    @Override
    public void onEvent(FlowableEvent flowableEvent) {
    	FlowableEventType type = flowableEvent.getType();
    	if (type == FlowableEngineEventType.TASK_CREATED) { 
	        TaskEntity taskEntity = (TaskEntity) ((FlowableEntityEventImpl) flowableEvent).getEntity();
	        String taskId = taskEntity.getId();
	        String procInsId = taskEntity.getProcessInstanceId();
	        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
	                .processInstanceId(procInsId)
	                .singleResult();
			String businessKey =  historicProcessInstance.getBusinessKey();
			String deployId = historicProcessInstance.getDeploymentId();
	        
	        List<IdentityLink> idList = taskService.getIdentityLinksForTask(taskId);
	        if (CollectionUtils.isEmpty(idList)) {
	            return;
	        }
	        List<String> userNameList = new ArrayList<>();
	        // 获取接收人，此处从Identity获取，实际情况会更复杂
	        idList.forEach(identityLink -> {
	            if (StringUtils.isNotBlank(identityLink.getUserId())) {
	                userNameList.add(identityLink.getUserId());
	            }
	        });
	        
	        if (CollectionUtils.isNotEmpty(userNameList)) {
	            // TODO:  发送提醒消息
	        	SysUser loginUser = iFlowThirdService.getLoginUser();
	        	String taskMessageUrl;
	        	if(StringUtils.isNotBlank(businessKey)) {
	    			taskMessageUrl = "<a href=" + iFlowThirdService.getBaseUrl() + "?procInsId=" + procInsId + "&deployId=" 
	    				              + deployId + "&taskId=" + taskId + "&businessKey=" + businessKey
	    				              + "&finished=true" + ">点击这个进行处理</a>" ;
	    		}
	    		else {
	    			taskMessageUrl = "<a href=" + iFlowThirdService.getBaseUrl() + "?procInsId=" + procInsId + "&deployId=" 
	    		              + deployId + "&taskId=" + taskId + "&businessKey" + "&finished=true" + ">点击这个进行处理</a>" ;
	    		}
	        	String msgContent = "流程待办通知" + taskMessageUrl;
	        	userNameList.forEach(userName -> {
	        		iFlowThirdService.sendSysAnnouncement(loginUser.getUsername(), userName, "流程待办通知", msgContent, "3");//setMsgCategory=3是待办
	        	});
	        }
    	}
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}

