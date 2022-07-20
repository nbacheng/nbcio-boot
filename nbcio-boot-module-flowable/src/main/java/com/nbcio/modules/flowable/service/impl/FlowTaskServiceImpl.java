package com.nbcio.modules.flowable.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.UUIDGenerator;
import com.nbcio.modules.flowable.apithird.business.entity.FlowMyBusiness;
import com.nbcio.modules.flowable.apithird.business.service.impl.FlowMyBusinessServiceImpl;
import com.nbcio.modules.flowable.apithird.entity.ActStatus;
import com.nbcio.modules.flowable.apithird.entity.SysRole;
import com.nbcio.modules.flowable.apithird.entity.SysUser;
import com.nbcio.modules.flowable.apithird.entity.FlowCategory.Category;
import com.nbcio.modules.flowable.apithird.service.FlowCallBackServiceI;
import com.nbcio.modules.flowable.apithird.service.IFlowThirdService;
import com.nbcio.modules.flowable.common.constant.ProcessConstants;
import com.nbcio.modules.flowable.common.enums.FlowComment;
import com.nbcio.modules.flowable.common.exception.CustomException;
import com.nbcio.modules.flowable.domain.dto.FlowCommentDto;
import com.nbcio.modules.flowable.domain.dto.FlowCommentFileDto;
import com.nbcio.modules.flowable.domain.dto.FlowNextDto;
import com.nbcio.modules.flowable.domain.dto.FlowTaskDto;
import com.nbcio.modules.flowable.domain.dto.FlowViewerDto;
import com.nbcio.modules.flowable.domain.vo.FlowTaskVo;
import com.nbcio.modules.flowable.entity.SysForm;
import com.nbcio.modules.flowable.factory.FlowServiceFactory;
import com.nbcio.modules.flowable.flow.CustomProcessDiagramGenerator;
import com.nbcio.modules.flowable.flow.FindNextNodeUtil;
import com.nbcio.modules.flowable.flow.FlowableUtils;
import com.nbcio.modules.flowable.service.IFlowTaskService;
import com.nbcio.modules.flowable.service.ISysDeployFormService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sound.midi.SoundbankResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 **/
@Service
@Slf4j
@Transactional
public class FlowTaskServiceImpl extends FlowServiceFactory implements IFlowTaskService {

    @Resource
    private IFlowThirdService iFlowThirdService;
    
    @Autowired
    private ISysDeployFormService sysInstanceFormService;
    
    @Autowired
    FlowMyBusinessServiceImpl flowMyBusinessService;
    /**
     * 完成任务
     *
     * @param taskVo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result complete(FlowTaskVo taskVo) {
    	
    	//如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
    	if(StrUtil.isNotBlank(taskVo.getDataId())){
    		FlowMyBusiness business = flowMyBusinessService.getByDataId(taskVo.getDataId());
            taskVo.setTaskId(business.getTaskId());
            taskVo.setInstanceId(business.getProcessInstanceId());
            return this.completeForDataID(taskVo);
    	}

        Task task = taskService.createTaskQuery().taskId(taskVo.getTaskId()).singleResult();
        //TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskVo.getTaskId()).singleResult();
        if (Objects.isNull(task)){
            return Result.error("任务不存在");
        }
        SysUser loginUser = iFlowThirdService.getLoginUser();
        FlowCommentFileDto commentfiles = taskVo.getCommentFileDto();
        
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.DELEGATE.getType(), taskService.getVariable(taskVo.getTaskId(), "delegate").toString()+ taskVo.getComment());
            taskService.setVariable(taskVo.getTaskId(), "delegate", loginUser.getRealname() + FlowComment.DELEGATE.getRemark() + ":" +  taskVo.getComment());
            if(StringUtils.isNoneEmpty(commentfiles.getFileurl())) {
        		//以逗号分割的多个文件链接地址与文件名称
        		
        		String attachmentDescription = getFileName(commentfiles);
        		//这里文件类型先作为流程类型来处理，以便显示时候可以区分显示不同的附件	
    			String fileType = FlowComment.DELEGATE.getType();
				taskService.createAttachment(fileType, taskVo.getTaskId(), taskVo.getInstanceId(), attachmentDescription, attachmentDescription, commentfiles.getFileurl());  
        	}
            //生成子流程历史记录，目前显示还有问题
            //TaskEntity subtask = createSubTask(taskEntity, taskEntity.getId(), loginUser.getUsername());
            //taskService.complete(subtask.getId());
            taskService.resolveTask(taskVo.getTaskId(), taskVo.getValues());
        } else {
        	if(Objects.nonNull(taskService.getVariable(taskVo.getTaskId(), "assign"))) {//转办过来的流程任务
        		taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.ASSIGN.getType(), taskService.getVariable(taskVo.getTaskId(), "assign").toString()+ taskVo.getComment());
        		taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.NORMAL.getType(), loginUser.getRealname() + "意见:" + taskVo.getComment());
        		if(StringUtils.isNoneEmpty(commentfiles.getFileurl())) {
        			//以逗号分割的多个文件链接地址与文件名称
            		
            		String attachmentDescription = getFileName(commentfiles);
            		//这里文件类型先作为流程类型来处理，以便显示时候可以区分显示不同的附件	
        			String fileType = FlowComment.NORMAL.getType();
        			taskService.createAttachment(fileType, taskVo.getTaskId(), taskVo.getInstanceId(), attachmentDescription, attachmentDescription, commentfiles.getFileurl());  
            	}
        	}
        	else if(Objects.nonNull(taskService.getVariable(taskVo.getTaskId(), "delegate"))) {//委派过来的流程任务
        		taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.NORMAL.getType(), loginUser.getRealname() + "意见:" + taskVo.getComment());
        		if(StringUtils.isNoneEmpty(commentfiles.getFileurl())) {
            		///以逗号分割的多个文件链接地址与文件名称
            		
            		String attachmentDescription = getFileName(commentfiles);
            		//这里文件类型先作为流程类型来处理，以便显示时候可以区分显示不同的附件	
        			String fileType = FlowComment.NORMAL.getType();
        			taskService.createAttachment(fileType, taskVo.getTaskId(), taskVo.getInstanceId(), attachmentDescription, attachmentDescription, commentfiles.getFileurl());  
            	}
        	}
        	else {
        		 taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.NORMAL.getType(), taskVo.getComment());
        		 if(StringUtils.isNoneEmpty(commentfiles.getFileurl())) {
        			//以逗号分割的多个文件链接地址与文件名称
             		
             		String attachmentDescription = getFileName(commentfiles);
             		//这里文件类型先作为流程类型来处理，以便显示时候可以区分显示不同的附件	
             		String fileType = FlowComment.NORMAL.getType();
             		taskService.createAttachment(fileType, taskVo.getTaskId(), taskVo.getInstanceId(), attachmentDescription, attachmentDescription, commentfiles.getFileurl());  
             	}
        	}
           
            taskService.setAssignee(taskVo.getTaskId(), loginUser.getUsername());
            //complete方法来流传任务到下一个节点。其中第二个参数variables为下一个节点需要的参数Map,主要指流程设置的一些变量信息
            taskService.complete(taskVo.getTaskId(), taskVo.getValues());
            if(taskVo.getValues() !=null) {
              Task tasknext = taskService.createTaskQuery().processInstanceId(taskVo.getInstanceId()).active().singleResult();
              if(taskVo.getValues().containsKey("approval")) {//前端传回的变量值
            	 taskService.setAssignee(tasknext.getId(), taskVo.getValues().get("approval").toString());
              }
            }   
        }
        
        return Result.OK("任务完成");
    }
    

	/**
     * 完成任务
     *
     * @param taskVo 请求实体参数，有业务数据dataid
     */
    public Result completeForDataID(FlowTaskVo taskVo) {
        Task task = taskService.createTaskQuery().taskId(taskVo.getTaskId()).singleResult();
        if (Objects.isNull(task)){
            return Result.error("任务不存在");
        }
        SysUser loginUser = iFlowThirdService.getLoginUser();
        FlowCommentFileDto commentfiles = taskVo.getCommentFileDto();
        if (DelegationState.PENDING.equals(task.getDelegationState())) { //对于委派的处理
        	 taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.DELEGATE.getType(), taskService.getVariable(taskVo.getTaskId(), "delegate").toString()+ taskVo.getComment());
             taskService.setVariable(taskVo.getTaskId(), "delegate", loginUser.getRealname() + FlowComment.DELEGATE.getRemark() + ":" +  taskVo.getComment());      	
             if(StringUtils.isNoneEmpty(commentfiles.getFileurl())) {
            	//以逗号分割的多个文件链接地址与文件名称
         		
         		String attachmentDescription = getFileName(commentfiles);
         		//这里文件类型先作为流程类型来处理，以便显示时候可以区分显示不同的附件	
     			String fileType = FlowComment.DELEGATE.getType();
 				taskService.createAttachment(fileType, taskVo.getTaskId(), taskVo.getInstanceId(), attachmentDescription, attachmentDescription, commentfiles.getFileurl());  
         	}
             //taskService.resolveTask(taskVo.getTaskId(), taskVo.getValues());
            //对委派的自定义业务dataid进行处理
          //业务数据id
            String dataId = taskVo.getDataId();
            //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
            FlowMyBusiness business = flowMyBusinessService.getByDataId(dataId);
            //spring容器类名
            String serviceImplName = business.getServiceImplName();
            FlowCallBackServiceI flowCallBackService = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplName);
            // 流程变量,目前没有实现，flowValuesOfTask返回null
            Map<String, Object> flowBeforeParamsValues = flowCallBackService.flowValuesOfTask(business.getTaskNameId(),taskVo.getValues());

            //设置数据
            Map<String, Object> values = taskVo.getValues();
            if (MapUtil.isNotEmpty(flowBeforeParamsValues)){
            //    业务层有设置变量，使用业务层的变量
                values = flowBeforeParamsValues;
            }
           
            // 被委派任务的办理: 办理完成后，委派任务会自动回到委派人的任务中
            //处理下个节点的候选人，对于委派来说，实际上就是当前节点，因为委派完成后相当于驳回到当前节点了
            taskService.resolveTask(taskVo.getTaskId(), values);
                      
            String doneUsers = business.getDoneUsers();
            // 处理过流程的人
            JSONArray doneUserList = new JSONArray();
            if (StrUtil.isNotBlank(doneUsers)){
                doneUserList = JSON.parseArray(doneUsers);
            }
            if (!doneUserList.contains(loginUser.getUsername())){
                doneUserList.add(loginUser.getUsername());
            }
            
            Task task2 = taskService.createTaskQuery().processInstanceId(business.getProcessInstanceId()).active().singleResult();
            
            SysUser sysUser = iFlowThirdService.getUserByUsername(task2.getAssignee());
            business.setActStatus(ActStatus.doing)
                    .setTaskId(task2.getId())
                    .setTaskNameId(task2.getId())
                    .setTaskName(task2.getName())
                    .setPriority(Integer.toString(task2.getPriority()))       
                    .setDoneUsers(doneUserList.toString())
                    .setTodoUsers(sysUser.getRealname());
            flowMyBusinessService.updateById(business);
            // 流程处理完后，进行回调业务层
            business.setValues(values);
            if (flowCallBackService!=null)flowCallBackService.afterFlowHandle(business);
            return Result.OK("任务完成");
            
        } else {//其它正常流程的处理
        	if(Objects.nonNull(taskService.getVariable(taskVo.getTaskId(), "assign"))) {//转办过来的流程任务
        		taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.ASSIGN.getType(), taskService.getVariable(taskVo.getTaskId(), "assign").toString()+ taskVo.getComment());
        		taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.NORMAL.getType(), loginUser.getRealname() + "意见:" + taskVo.getComment());
        		if(StringUtils.isNoneEmpty(commentfiles.getFileurl())) {
        			//以逗号分割的多个文件链接地址与文件名称
            		
            		String attachmentDescription = getFileName(commentfiles);
            		//这里文件类型先作为流程类型来处理，以便显示时候可以区分显示不同的附件	
        			String fileType = FlowComment.NORMAL.getType();
        			taskService.createAttachment(fileType, taskVo.getTaskId(), taskVo.getInstanceId(), attachmentDescription, attachmentDescription, commentfiles.getFileurl());  
            	}
        	}
        	else if(Objects.nonNull(taskService.getVariable(taskVo.getTaskId(), "delegate"))) {//委派过来的流程任务
        		taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.NORMAL.getType(), loginUser.getRealname() + "意见:" + taskVo.getComment());
        		if(StringUtils.isNoneEmpty(commentfiles.getFileurl())) {
        			//以逗号分割的多个文件链接地址与文件名称
            		
            		String attachmentDescription = getFileName(commentfiles);
            		//这里文件类型先作为流程类型来处理，以便显示时候可以区分显示不同的附件	
        			String fileType = FlowComment.NORMAL.getType();
        			taskService.createAttachment(fileType, taskVo.getTaskId(), taskVo.getInstanceId(), attachmentDescription, attachmentDescription, commentfiles.getFileurl());  
            	}
        	}
        	else {
        		 taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.NORMAL.getType(), taskVo.getComment());
        		 if(StringUtils.isNoneEmpty(commentfiles.getFileurl())) {
        			//以逗号分割的多个文件链接地址与文件名称
             		
             		String attachmentDescription = getFileName(commentfiles);
              		//这里文件类型先作为流程类型来处理，以便显示时候可以区分显示不同的附件	
              		String fileType = FlowComment.NORMAL.getType();
              		taskService.createAttachment(fileType, taskVo.getTaskId(), taskVo.getInstanceId(), attachmentDescription, attachmentDescription, commentfiles.getFileurl());  
              	}
        	}
            //taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.NORMAL.getType(), taskVo.getComment());
            taskService.setAssignee(taskVo.getTaskId(), loginUser.getUsername());
            //taskService.complete(taskVo.getTaskId(), taskVo.getValues());
        }
        /*======================审批通过  回调以及关键数据保存======================*/
        //业务数据id
        String dataId = taskVo.getDataId();
        //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
        FlowMyBusiness business = flowMyBusinessService.getByDataId(dataId);
        //spring容器类名
        String serviceImplName = business.getServiceImplName();
        FlowCallBackServiceI flowCallBackService = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplName);
        // 流程变量
        Map<String, Object> flowBeforeParamsValues = flowCallBackService.flowValuesOfTask(business.getTaskNameId(),taskVo.getValues());

        //设置数据
        Map<String, Object> values = taskVo.getValues();
        if (MapUtil.isNotEmpty(flowBeforeParamsValues)){
        //    业务层有设置变量，使用业务层的变量
            values = flowBeforeParamsValues;
        }
             
        FlowNextDto nextFlowNode = this.getNextFlowNode(task.getId(), values);
        
        taskService.complete(taskVo.getTaskId(), values);
        
        if(taskVo.getValues() !=null) {//对候选组人员选择的支持
            Task tasknext = taskService.createTaskQuery().processInstanceId(taskVo.getInstanceId()).active().singleResult();
            if(taskVo.getValues().containsKey("approval")) {//前端传回的变量值
          	 taskService.setAssignee(tasknext.getId(), taskVo.getValues().get("approval").toString());
            }
        }   
        //下一个实例节点
        Task task2 = taskService.createTaskQuery().processInstanceId(business.getProcessInstanceId()).active().singleResult();
        // 下个节点候选人，目前没有实现这功能，返回null
        List<String> beforeParamsCandidateUsernames = Lists.newArrayList();
        if(task2!=null){
            beforeParamsCandidateUsernames = flowCallBackService.flowCandidateUsernamesOfTask(task2.getTaskDefinitionKey(),taskVo.getValues());
        }
        
        String doneUsers = business.getDoneUsers();
        // 处理过流程的人
        JSONArray doneUserList = new JSONArray();
        if (StrUtil.isNotBlank(doneUsers)){
            doneUserList = JSON.parseArray(doneUsers);
        }
        if (!doneUserList.contains(loginUser.getUsername())){
            doneUserList.add(loginUser.getUsername());
        }
        if (nextFlowNode!=null){
            //**有下一个节点
            UserTask nextTask = nextFlowNode.getUserTask();
            //能够处理下个节点的候选人
            List<SysUser> nextFlowNodeUserList = nextFlowNode.getUserList();
            List<String> collect_username = nextFlowNodeUserList.stream().map(SysUser::getUsername).collect(Collectors.toList());
            //collect_username转换成realname
            List<String> newusername = new ArrayList<String>();
            if(taskVo.getValues().containsKey("approval")) {//前端传回的变量值
            	SysUser sysUser = iFlowThirdService.getUserByUsername(taskVo.getValues().get("approval").toString());
            	newusername.add(sysUser.getRealname());
            }
            else {
            	for (String oldUser : collect_username) {
            	SysUser sysUser = iFlowThirdService.getUserByUsername(oldUser);
                newusername.add(sysUser.getRealname());
                }
            }
            business.setActStatus(ActStatus.doing)
                    .setTaskId(task2.getId())
                    .setTaskNameId(nextTask.getId())
                    .setTaskName(nextTask.getName())
                    .setPriority(nextTask.getPriority())
                    .setDoneUsers(doneUserList.toJSONString())
                    .setTodoUsers(JSON.toJSONString(newusername))
                ;
            // 删除后重写
            for (String oldUser : collect_username) {
                taskService.deleteCandidateUser(task2.getId(),oldUser);
            }
            if (CollUtil.isNotEmpty(beforeParamsCandidateUsernames)){
                // 业务层有指定候选人，覆盖
                for (String newUser : beforeParamsCandidateUsernames) {
                    taskService.addCandidateUser(task2.getId(),newUser);
                }
                business.setTodoUsers(JSON.toJSONString(beforeParamsCandidateUsernames));
            } else {
                for (String oldUser : collect_username) {
                    taskService.addCandidateUser(task2.getId(),oldUser);
                }
            }

        } else {
            //    **没有下一个节点，流程已经结束了
            business.setActStatus(ActStatus.pass)
                    .setDoneUsers(doneUserList.toJSONString())
                    .setTodoUsers("")
                    .setTaskId("")
                    .setTaskNameId("")
                    .setTaskName("")
            ;
        }
        flowMyBusinessService.updateById(business);
        // 流程处理完后，进行回调业务层
        business.setValues(values);
        if (flowCallBackService!=null)flowCallBackService.afterFlowHandle(business);
        return Result.OK("任务完成");
    }
   
    /**
     * 返回文件名
     *
     * @param commentfiles
     */
       String  getFileName(FlowCommentFileDto commentfiles) {
    	 //以逗号分割的多个文件链接地址与文件名称
    	 String attachmentDescription = "";  
    	 if(commentfiles.getFileurl().contains(",")) {
    		 String[] strUrls = commentfiles.getFileurl().split(",");
 		     for(String url : strUrls){
 			    attachmentDescription = StringUtils.substringAfterLast(StringUtils.substringBefore(url, "_"), "/")+ "," + attachmentDescription ;
 		     }
    	 }
    	 else {
    		 attachmentDescription = StringUtils.substringAfterLast(StringUtils.substringBefore(commentfiles.getFileurl(), "_"), "/");
    	 }
		 return attachmentDescription;
       }
    /**
     * 驳回任务
     *
     * @param flowTaskVo
     */
    @Override
    public void taskReject(FlowTaskVo flowTaskVo) {
    	if(StrUtil.isNotBlank(flowTaskVo.getDataId())){
    		 FlowMyBusiness business = flowMyBusinessService.getByDataId(flowTaskVo.getDataId());
             flowTaskVo.setTaskId(business.getTaskId());
             this.taskRejectForDataId(flowTaskVo);
             return;
    	}
    	
        if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
            throw new CustomException("任务处于挂起状态");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    // 获取节点信息
                    source = flowElement;
                }
            }
        }

        // 目的获取所有跳转到的节点 targetIds
        // 获取当前节点的所有父级用户任务节点
        // 深度优先算法思想：延边迭代深入
        List<UserTask> parentUserTaskList = FlowableUtils.iteratorFindParentUserTasks(source, null, null);
        if (parentUserTaskList == null || parentUserTaskList.size() == 0) {
            throw new CustomException("当前节点为初始任务节点，不能驳回");
        }
        // 获取活动 ID 即节点 Key
        List<String> parentUserTaskKeyList = new ArrayList<>();
        parentUserTaskList.forEach(item -> parentUserTaskKeyList.add(item.getId()));
        // 获取全部历史节点活动实例，即已经走过的节点历史，数据采用开始时间升序
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByHistoricTaskInstanceStartTime().asc().list();
        // 数据清洗，将回滚导致的脏数据清洗掉
        List<String> lastHistoricTaskInstanceList = FlowableUtils.historicTaskInstanceClean(allElements, historicTaskInstanceList);
        // 此时历史任务实例为倒序，获取最后走的节点
        List<String> targetIds = new ArrayList<>();
        // 循环结束标识，遇到当前目标节点的次数
        int number = 0;
        StringBuilder parentHistoricTaskKey = new StringBuilder();
        for (String historicTaskInstanceKey : lastHistoricTaskInstanceList) {
            // 当会签时候会出现特殊的，连续都是同一个节点历史数据的情况，这种时候跳过
            if (parentHistoricTaskKey.toString().equals(historicTaskInstanceKey)) {
                continue;
            }
            parentHistoricTaskKey = new StringBuilder(historicTaskInstanceKey);
            if (historicTaskInstanceKey.equals(task.getTaskDefinitionKey())) {
                number++;
            }
            // 在数据清洗后，历史节点就是唯一一条从起始到当前节点的历史记录，理论上每个点只会出现一次
            // 在流程中如果出现循环，那么每次循环中间的点也只会出现一次，再出现就是下次循环
            // number == 1，第一次遇到当前节点
            // number == 2，第二次遇到，代表最后一次的循环范围
            if (number == 2) {
                break;
            }
            // 如果当前历史节点，属于父级的节点，说明最后一次经过了这个点，需要退回这个点
            if (parentUserTaskKeyList.contains(historicTaskInstanceKey)) {
                targetIds.add(historicTaskInstanceKey);
            }
        }


        // 目的获取所有需要被跳转的节点 currentIds
        // 取其中一个父级任务，因为后续要么存在公共网关，要么就是串行公共线路
        UserTask oneUserTask = parentUserTaskList.get(0);
        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需驳回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要撤回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(oneUserTask, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));


        // 规定：并行网关之前节点必须需存在唯一用户任务节点，如果出现多个任务节点，则并行网关节点默认为结束节点，原因为不考虑多对多情况
        if (targetIds.size() > 1 && currentIds.size() > 1) {
            throw new CustomException("任务出现多对多情况，无法撤回");
        }

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // 设置驳回意见
        currentTaskIds.forEach(item -> taskService.addComment(item, task.getProcessInstanceId(), FlowComment.REJECT.getType(), flowTaskVo.getComment()));
        
        SysUser loginUser = iFlowThirdService.getLoginUser();
        try {
            // 设置处理人
            taskService.setAssignee(task.getId(), loginUser.getUsername());
            // 如果父级任务多于 1 个，说明当前节点不是并行节点，原因为不考虑多对多情况
            if (targetIds.size() > 1) {
                // 1 对 多任务跳转，currentIds 当前节点(1)，targetIds 跳转到的节点(多)
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(task.getProcessInstanceId()).
                        moveSingleActivityIdToActivityIds(currentIds.get(0), targetIds).changeState();
            }
            // 如果父级任务只有一个，因此当前任务可能为网关中的任务
            if (targetIds.size() == 1) {
                // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetIds.get(0) 跳转到的节点(1)
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(task.getProcessInstanceId())
                        .moveActivityIdsToSingleActivityId(currentIds, targetIds.get(0)).changeState();
            }
            
            // 驳回到了上一个节点等待处理
            Task targetTask = taskService.createTaskQuery().processInstanceId(flowTaskVo.getInstanceId()).active().singleResult();
            FlowElement targetElement = null;
            if (allElements != null) {
                for (FlowElement flowElement : allElements) {
                    // 类型为用户节点
                    if (flowElement.getId().equals(targetTask.getTaskDefinitionKey())) {
                        // 获取节点信息
                        targetElement = flowElement;
                    }
                }
            }

         // 流程发起人
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(targetTask.getProcessInstanceId()).singleResult();
            String startUserId = processInstance.getStartUserId();
            
            if (targetElement!=null){
                UserTask targetUserTask = (UserTask) targetElement;
                
                if (StrUtil.equals(targetUserTask.getIncomingFlows().get(0).getSourceRef(),"startNode1")) {//是否为发起人节点
                    //开始节点 设置处理人为申请人
                    taskService.setAssignee(targetTask.getId(), startUserId);
                } else {
                    List<SysUser> sysUserFromTask = getSysUserFromTask(targetUserTask);
                    List<String> collect_username = sysUserFromTask.stream().map(SysUser::getUsername).collect(Collectors.toList());
                    //collect_username转换成realname
                    List<String> newusername = new ArrayList<String>();
                    for (String oldUser : collect_username) {
                    	SysUser sysUser = iFlowThirdService.getUserByUsername(oldUser);
                        newusername.add(sysUser.getRealname());
                    }
             
                    // 删除后重写
                    for (String oldUser : collect_username) {
                        taskService.deleteCandidateUser(targetTask.getId(),oldUser);
                    }
                  
                    for (String oldUser : collect_username) {
                        taskService.addCandidateUser(targetTask.getId(),oldUser);
                    }
                }
            }
            
        } catch (FlowableObjectNotFoundException e) {
            throw new CustomException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new CustomException("无法取消或开始活动");
        }

    }
    
    /**
     * 驳回任务
     *
     * @param flowTaskVo
     */
    @Override
    public void taskRejectForDataId(FlowTaskVo flowTaskVo) {
        if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
            throw new CustomException("任务处于挂起状态");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    // 获取节点信息
                    source = flowElement;
                }
            }
        }

        // 目的获取所有跳转到的节点 targetIds
        // 获取当前节点的所有父级用户任务节点
        // 深度优先算法思想：延边迭代深入
        List<UserTask> parentUserTaskList = FlowableUtils.iteratorFindParentUserTasks(source, null, null);
        if (parentUserTaskList == null || parentUserTaskList.size() == 0) {
            throw new CustomException("当前节点为初始任务节点，不能驳回");
        }
        // 获取活动 ID 即节点 Key
        List<String> parentUserTaskKeyList = new ArrayList<>();
        parentUserTaskList.forEach(item -> parentUserTaskKeyList.add(item.getId()));
        // 获取全部历史节点活动实例，即已经走过的节点历史，数据采用开始时间升序
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByHistoricTaskInstanceStartTime().asc().list();
        // 数据清洗，将回滚导致的脏数据清洗掉
        List<String> lastHistoricTaskInstanceList = FlowableUtils.historicTaskInstanceClean(allElements, historicTaskInstanceList);
        // 此时历史任务实例为倒序，获取最后走的节点
        List<String> targetIds = new ArrayList<>();
        // 循环结束标识，遇到当前目标节点的次数
        int number = 0;
        StringBuilder parentHistoricTaskKey = new StringBuilder();
        for (String historicTaskInstanceKey : lastHistoricTaskInstanceList) {
            // 当会签时候会出现特殊的，连续都是同一个节点历史数据的情况，这种时候跳过
            if (parentHistoricTaskKey.toString().equals(historicTaskInstanceKey)) {
                continue;
            }
            parentHistoricTaskKey = new StringBuilder(historicTaskInstanceKey);
            if (historicTaskInstanceKey.equals(task.getTaskDefinitionKey())) {
                number++;
            }
            // 在数据清洗后，历史节点就是唯一一条从起始到当前节点的历史记录，理论上每个点只会出现一次
            // 在流程中如果出现循环，那么每次循环中间的点也只会出现一次，再出现就是下次循环
            // number == 1，第一次遇到当前节点
            // number == 2，第二次遇到，代表最后一次的循环范围
            if (number == 2) {
                break;
            }
            // 如果当前历史节点，属于父级的节点，说明最后一次经过了这个点，需要退回这个点
            if (parentUserTaskKeyList.contains(historicTaskInstanceKey)) {
                targetIds.add(historicTaskInstanceKey);
            }
        }


        // 目的获取所有需要被跳转的节点 currentIds
        // 取其中一个父级任务，因为后续要么存在公共网关，要么就是串行公共线路
        UserTask oneUserTask = parentUserTaskList.get(0);
        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需驳回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要撤回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(oneUserTask, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));


        // 规定：并行网关之前节点必须需存在唯一用户任务节点，如果出现多个任务节点，则并行网关节点默认为结束节点，原因为不考虑多对多情况
        if (targetIds.size() > 1 && currentIds.size() > 1) {
            throw new CustomException("任务出现多对多情况，无法撤回");
        }

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // 设置驳回意见
        currentTaskIds.forEach(item -> taskService.addComment(item, task.getProcessInstanceId(), FlowComment.REJECT.getType(), flowTaskVo.getComment()));
        SysUser loginUser = iFlowThirdService.getLoginUser();
        try {
            // 设置处理人
            taskService.setAssignee(task.getId(), loginUser.getUsername());
            // 如果父级任务多于 1 个，说明当前节点不是并行节点，原因为不考虑多对多情况
            if (targetIds.size() > 1) {
                // 1 对 多任务跳转，currentIds 当前节点(1)，targetIds 跳转到的节点(多)
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(task.getProcessInstanceId()).
                        moveSingleActivityIdToActivityIds(currentIds.get(0), targetIds).changeState();
            }
            // 如果父级任务只有一个，因此当前任务可能为网关中的任务
            if (targetIds.size() == 1) {
                // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetIds.get(0) 跳转到的节点(1)
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(task.getProcessInstanceId())
                        .moveActivityIdsToSingleActivityId(currentIds, targetIds.get(0)).changeState();
            }
            /*======================驳回  回调以及关键数据保存======================*/
            //业务数据id
            String dataId = flowTaskVo.getDataId();
            if (dataId==null) return;
            //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
            FlowMyBusiness business = flowMyBusinessService.getByDataId(dataId);
            // 驳回到了上一个节点等待处理
            Task targetTask = taskService.createTaskQuery().processInstanceId(business.getProcessInstanceId()).active().singleResult();
            //spring容器类名
            String serviceImplName = business.getServiceImplName();
            FlowCallBackServiceI flowCallBackService = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplName);
            Map<String, Object> values = flowTaskVo.getValues();
            if (values ==null){
                values = MapUtil.newHashMap();
                values.put("dataId",dataId);
            } else {
                values.put("dataId",dataId);
            }
            List<String> beforeParamsCandidateUsernames = flowCallBackService.flowCandidateUsernamesOfTask(targetTask.getTaskDefinitionKey(), values);
            //设置数据
            String doneUsers = business.getDoneUsers();
            // 处理过流程的人
            JSONArray doneUserList = new JSONArray();
            if (StrUtil.isNotBlank(doneUsers)){
                doneUserList = JSON.parseArray(doneUsers);
            }
            if (!doneUserList.contains(loginUser.getUsername())){
                doneUserList.add(loginUser.getUsername());
            }
            business.setActStatus(ActStatus.reject)
                    .setTaskId(targetTask.getId())
                    .setTaskNameId(targetTask.getTaskDefinitionKey())
                    .setTaskName(targetTask.getName())
                    .setDoneUsers(doneUserList.toJSONString())
            ;
            FlowElement targetElement = null;
            if (allElements != null) {
                for (FlowElement flowElement : allElements) {
                    // 类型为用户节点
                    if (flowElement.getId().equals(targetTask.getTaskDefinitionKey())) {
                        // 获取节点信息
                        targetElement = flowElement;
                    } 
                }
            }

            if (targetElement!=null){
                UserTask targetUserTask = (UserTask) targetElement;
                business.setPriority(targetUserTask.getPriority());

                if (StrUtil.equals(targetUserTask.getIncomingFlows().get(0).getSourceRef(),"startNode1")) {//是否为发起人节点
                    //    开始节点。设置处理人为申请人
                    business.setTodoUsers(JSON.toJSONString(Lists.newArrayList(business.getProposer())));
                    taskService.setAssignee(business.getTaskId(),business.getProposer());
                } else {
                    List<SysUser> sysUserFromTask = getSysUserFromTask(targetUserTask);
                    List<String> collect_username = sysUserFromTask.stream().map(SysUser::getUsername).collect(Collectors.toList());
                    //collect_username转换成realname
                    List<String> newusername = new ArrayList<String>();
                    for (String oldUser : collect_username) {
                    	SysUser sysUser = iFlowThirdService.getUserByUsername(oldUser);
                        newusername.add(sysUser.getRealname());
                    }
                    business.setTodoUsers(JSON.toJSONString(newusername));
                    // 删除后重写
                    for (String oldUser : collect_username) {
                        taskService.deleteCandidateUser(targetTask.getId(),oldUser);
                    }
                    if (CollUtil.isNotEmpty(beforeParamsCandidateUsernames)){
                        // 业务层有指定候选人，覆盖
                        for (String newUser : beforeParamsCandidateUsernames) {
                            taskService.addCandidateUser(targetTask.getId(),newUser);
                        }
                        business.setTodoUsers(JSON.toJSONString(beforeParamsCandidateUsernames));
                    } else {
                        for (String oldUser : collect_username) {
                            taskService.addCandidateUser(targetTask.getId(),oldUser);
                        }
                    }
                }
            }

            flowMyBusinessService.updateById(business);
           // 流程处理完后，进行回调业务层
            business.setValues(values);
            if (flowCallBackService!=null) flowCallBackService.afterFlowHandle(business);
        } catch (FlowableObjectNotFoundException e) {
            throw new CustomException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new CustomException("无法取消或开始活动");
        }

    }
    
    /**
     * 退回任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void taskReturn(FlowTaskVo flowTaskVo) {
    	 //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
    	if(StrUtil.isNotBlank(flowTaskVo.getDataId())){
    		FlowMyBusiness business = flowMyBusinessService.getByDataId(flowTaskVo.getDataId());
            flowTaskVo.setTaskId(business.getTaskId());
            taskReturnForDataId(flowTaskVo);
            return;
    	}

        if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
            throw new CustomException("任务处于挂起状态");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        // 获取跳转的节点元素
        FlowElement target = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 当前任务节点元素
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = flowElement;
                }
                // 跳转的节点元素
                if (flowElement.getId().equals(flowTaskVo.getTargetKey())) {
                    target = flowElement;
                }
            }
        }

        // 从当前节点向前扫描
        // 如果存在路线上不存在目标节点，说明目标节点是在网关上或非同一路线上，不可跳转
        // 否则目标节点相对于当前节点，属于串行
        Boolean isSequential = FlowableUtils.iteratorCheckSequentialReferTarget(source, flowTaskVo.getTargetKey(), null, null);
        if (!isSequential) {
            throw new CustomException("当前节点相对于目标节点，不属于串行关系，无法回退");
        }


        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要退回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需退回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要退回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(target, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> {
            currentIds.add(item.getId());
        });

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // 设置回退意见
        for (String currentTaskId : currentTaskIds) {
            taskService.addComment(currentTaskId, task.getProcessInstanceId(), FlowComment.REBACK.getType(), flowTaskVo.getComment());
        }
        SysUser loginUser = iFlowThirdService.getLoginUser();
        try {
            // 设置处理人
            taskService.setAssignee(task.getId(), loginUser.getUsername());
            // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetKey 跳转到的节点(1)
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId())
                    .moveActivityIdsToSingleActivityId(currentIds, flowTaskVo.getTargetKey()).changeState();
          //**跳转到目标节点
            Task targetTask = taskService.createTaskQuery().processInstanceId(flowTaskVo.getInstanceId()).active().singleResult();
         // 流程发起人
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(targetTask.getProcessInstanceId()).singleResult();
            String startUserId = processInstance.getStartUserId();
            FlowElement targetElement = null;
            if (allElements != null) {
                for (FlowElement flowElement : allElements) {
                    // 类型为用户节点
                    if (flowElement.getId().equals(targetTask.getTaskDefinitionKey())) {
                        // 获取节点信息
                        targetElement = flowElement;
                    }
                }
            }
            if (targetElement!=null){
                UserTask targetUserTask = (UserTask) targetElement;
                
                if (StrUtil.equals(targetUserTask.getIncomingFlows().get(0).getSourceRef(),"startNode1")) {//是否为发起人节点
                    //开始节点 设置处理人为申请人
                    taskService.setAssignee(targetTask.getId(), startUserId);
                } else {
                    List<SysUser> sysUserFromTask = getSysUserFromTask(targetUserTask);
                    List<String> collect_username = sysUserFromTask.stream().map(SysUser::getUsername).collect(Collectors.toList());
                    //collect_username转换成realname
                    List<String> newusername = new ArrayList<String>();
                    for (String oldUser : collect_username) {
                    	SysUser sysUser = iFlowThirdService.getUserByUsername(oldUser);
                        newusername.add(sysUser.getRealname());
                    }
             
                    // 删除后重写
                    for (String oldUser : collect_username) {
                        taskService.deleteCandidateUser(targetTask.getId(),oldUser);
                    }
                  
                    for (String oldUser : collect_username) {
                        taskService.addCandidateUser(targetTask.getId(),oldUser);
                    }
                }
            }
            
        } catch (FlowableObjectNotFoundException e) {
            throw new CustomException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new CustomException("无法取消或开始活动");
        }
        
    }
    
    /**
     * 退回任务
     *
     * @param flowTaskVo 请求实体参数 ，请求业务DataId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void taskReturnForDataId(FlowTaskVo flowTaskVo) {
        if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
            throw new CustomException("任务处于挂起状态");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        // 获取跳转的节点元素
        FlowElement target = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 当前任务节点元素
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = flowElement;
                }
                // 跳转的节点元素
                if (flowElement.getId().equals(flowTaskVo.getTargetKey())) {
                    target = flowElement;
                }
            }
        }

        // 从当前节点向前扫描
        // 如果存在路线上不存在目标节点，说明目标节点是在网关上或非同一路线上，不可跳转
        // 否则目标节点相对于当前节点，属于串行
        Boolean isSequential = FlowableUtils.iteratorCheckSequentialReferTarget(source, flowTaskVo.getTargetKey(), null, null);
        if (!isSequential) {
            throw new CustomException("当前节点相对于目标节点，不属于串行关系，无法回退");
        }


        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需退回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要撤回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(target, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> {
            currentIds.add(item.getId());
        });

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // 设置回退意见
        for (String currentTaskId : currentTaskIds) {
            taskService.addComment(currentTaskId, task.getProcessInstanceId(), FlowComment.REBACK.getType(), flowTaskVo.getComment());
        }
        SysUser loginUser = iFlowThirdService.getLoginUser();
        try {
            // 设置处理人
            taskService.setAssignee(task.getId(), loginUser.getUsername());
            // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetKey 跳转到的节点(1)
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId())
                    .moveActivityIdsToSingleActivityId(currentIds, flowTaskVo.getTargetKey()).changeState();

            /*======================退回  回调以及关键数据保存======================*/
            //业务数据id
            String dataId = flowTaskVo.getDataId();
            if (dataId==null) return;
            //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
            FlowMyBusiness business = flowMyBusinessService.getByDataId(dataId);
            //spring容器类名
            String serviceImplName = business.getServiceImplName();
            FlowCallBackServiceI flowCallBackService = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplName);
            //设置数据
            String doneUsers = business.getDoneUsers();
            // 处理过流程的人
            JSONArray doneUserList = new JSONArray();
            if (StrUtil.isNotBlank(doneUsers)){
                doneUserList = JSON.parseArray(doneUsers);
            }

            if (!doneUserList.contains(loginUser.getUsername())){
                doneUserList.add(loginUser.getUsername());
            }
            //**跳转到目标节点
            Task targetTask = taskService.createTaskQuery().processInstanceId(business.getProcessInstanceId()).active().singleResult();
                business.setActStatus(ActStatus.reject)
                        .setTaskId(targetTask.getId())
                        .setTaskNameId(targetTask.getTaskDefinitionKey())
                        .setTaskName(targetTask.getName())
                        .setPriority(targetTask.getPriority()+"")
                        .setDoneUsers(doneUserList.toJSONString())
                ;
            if (target!=null){
                UserTask targetUserTask = (UserTask) target;
                business.setPriority(targetUserTask.getPriority());
                if (StrUtil.equals(targetUserTask.getIncomingFlows().get(0).getSourceRef(),"startNode1")) {//是否为发起人节点
                //    开始节点。设置处理人为申请人
                    business.setTodoUsers(JSON.toJSONString(Lists.newArrayList(business.getProposer())));
                    taskService.setAssignee(business.getTaskId(),business.getProposer());
                } else {
                    List<SysUser> sysUserFromTask = getSysUserFromTask(targetUserTask);
                    List<String> collect_username = sysUserFromTask.stream().map(SysUser::getUsername).collect(Collectors.toList());
                  //collect_username转换成realname
                    List<String> newusername = new ArrayList<String>();
                    for (String oldUser : collect_username) {
                    	SysUser sysUser = iFlowThirdService.getUserByUsername(oldUser);
                        newusername.add(sysUser.getRealname());
                    }
                    business.setTodoUsers(JSON.toJSONString(newusername));
                    // 删除后重写
                    for (String oldUser : collect_username) {
                        taskService.deleteCandidateUser(targetTask.getId(),oldUser);
                    }
                    Map<String, Object> values = flowTaskVo.getValues();
                    if (values==null){
                        values = MapUtil.newHashMap();
                        values.put("dataId",dataId);
                    } else {
                        values.put("dataId",dataId);
                    }
                    List<String> beforeParamsCandidateUsernames = flowCallBackService.flowCandidateUsernamesOfTask(targetTask.getTaskDefinitionKey(), values);
                    if (CollUtil.isNotEmpty(beforeParamsCandidateUsernames)){
                        // 业务层有指定候选人，覆盖
                        for (String newUser : beforeParamsCandidateUsernames) {
                            taskService.addCandidateUser(targetTask.getId(),newUser);
                        }
                        business.setTodoUsers(JSON.toJSONString(beforeParamsCandidateUsernames));
                    } else {
                        for (String oldUser : collect_username) {
                            taskService.addCandidateUser(targetTask.getId(),oldUser);
                        }
                    }
                }
            }
            flowMyBusinessService.updateById(business);
            // 流程处理完后，进行回调业务层
            business.setValues(flowTaskVo.getValues());
            if (flowCallBackService!=null) flowCallBackService.afterFlowHandle(business);
        } catch (FlowableObjectNotFoundException e) {
            throw new CustomException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new CustomException("无法取消或开始活动");
        }
    }

    /**
     * 获取所有可回退的节点
     *
     * @param flowTaskVo
     * @return
     */
    @Override
    public Result findReturnTaskList(FlowTaskVo flowTaskVo) {
    	
    	if(StrUtil.isNotBlank(flowTaskVo.getDataId())){
    		FlowMyBusiness business = flowMyBusinessService.getByDataId(flowTaskVo.getDataId());
            flowTaskVo.setTaskId(business.getTaskId());
            return findReturnTaskList(flowTaskVo);
    	}

        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息，暂不考虑子流程情况
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        // 获取当前任务节点元素
        UserTask source = null;
        if (flowElements != null) {
            for (FlowElement flowElement : flowElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = (UserTask) flowElement;
                }
            }
        }
        // 获取节点的所有路线
        List<List<UserTask>> roads = FlowableUtils.findRoad(source, null, null, null);
        // 可回退的节点列表
        List<UserTask> userTaskList = new ArrayList<>();
        for (List<UserTask> road : roads) {
            if (userTaskList.size() == 0) {
                // 还没有可回退节点直接添加
                userTaskList = road;
            } else {
                // 如果已有回退节点，则比对取交集部分
                userTaskList.retainAll(road);
            }
        }
        return Result.OK(userTaskList);
    }

    /**
     * 删除任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Override
    public void deleteTask(FlowTaskVo flowTaskVo) {
        // todo 待确认删除任务是物理删除任务 还是逻辑删除，让这个任务直接通过？
        taskService.deleteTask(flowTaskVo.getTaskId(),flowTaskVo.getComment());
    }

    /**
     * 认领/签收任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claim(FlowTaskVo flowTaskVo) {
        taskService.claim(flowTaskVo.getTaskId(), flowTaskVo.getUserId());
    }

    /**
     * 取消认领/签收任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unClaim(FlowTaskVo flowTaskVo) {
        taskService.unclaim(flowTaskVo.getTaskId());
    }

    /**
     * add by nbacheng
     * 委派任务,兼容自定义业务
     * 委派：是将任务节点分给其他人处理，等其他人处理好之后，委派任务会自动回到委派人的任务中
     * @param flowTaskVo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(FlowTaskVo flowTaskVo) {
    	 if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
             throw new CustomException("任务处于挂起状态");
         }
    	 
    	 SysUser targetUser = iFlowThirdService.getUserByUsername(flowTaskVo.getAssignee());
    	// 当前任务 task
         Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
         
    	 SysUser loginUser = iFlowThirdService.getLoginUser();
    	 String delegatecomment;
    	 if(task.getAssignee() != null ) {
    		 SysUser oldUser = iFlowThirdService.getUserByUsername(task.getAssignee());
             delegatecomment = oldUser.getRealname() + "经由"+ loginUser.getRealname() + "委派给" + targetUser.getRealname() + "意见:";
    		 delegatecomment = "经由"+ loginUser.getRealname() + "委派给" + targetUser.getRealname() + "意见:";
    	 }
    	 else {
    		 delegatecomment = "经由"+ loginUser.getRealname() + "委派给" + targetUser.getRealname() + "意见:";
    	 }
    	
    	 if(StrUtil.isNotBlank(flowTaskVo.getDataId())){
    		
    		
    		taskService.addComment(flowTaskVo.getTaskId(), flowTaskVo.getInstanceId(), FlowComment.DELEGATE.getType(), delegatecomment);
    		taskService.setVariable(flowTaskVo.getTaskId(), "delegate", delegatecomment);
    		taskService.delegateTask(flowTaskVo.getTaskId(), flowTaskVo.getAssignee());
    		/*======================退回  回调以及关键数据保存======================*/
            //业务数据id
            String dataId = flowTaskVo.getDataId();
            if (dataId==null) return;
            //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
            FlowMyBusiness business = flowMyBusinessService.getByDataId(dataId);
            //**获取当前节点
            Task currentTask = taskService.createTaskQuery().processInstanceId(business.getProcessInstanceId()).active().singleResult();
            
            //spring容器类名
            String serviceImplName = business.getServiceImplName();
            FlowCallBackServiceI flowCallBackService = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplName);
            
            String doneUsers = business.getDoneUsers();
            // 处理过流程的人
            JSONArray doneUserList = new JSONArray();
            if (StrUtil.isNotBlank(doneUsers)){
                doneUserList = JSON.parseArray(doneUsers);
            }
            if (!doneUserList.contains(loginUser.getUsername())){
                doneUserList.add(loginUser.getUsername());
            }
            
            business.setActStatus(ActStatus.delegate)
                    .setTaskId(currentTask.getId())
                    .setTaskNameId(currentTask.getTaskDefinitionKey())
                    .setTaskName(currentTask.getName())
                    .setDoneUsers(doneUserList.toJSONString())
                    .setTodoUsers(targetUser.getRealname())
            ;

            flowMyBusinessService.updateById(business);
            // 流程处理完后，进行回调业务层
            business.setValues(flowTaskVo.getValues());
            if (flowCallBackService!=null) flowCallBackService.afterFlowHandle(business);
    	}
    	else {
    		taskService.addComment(flowTaskVo.getTaskId(), flowTaskVo.getInstanceId(), FlowComment.DELEGATE.getType(), delegatecomment);
    		taskService.setVariable(flowTaskVo.getTaskId(), "delegate", delegatecomment);
    		taskService.delegateTask(flowTaskVo.getTaskId(), flowTaskVo.getAssignee());
    	}
        
    }

    
    /**
     * 创建子任务
     *
     * @param ptask    创建子任务
     * @param assignee 子任务的执行人
     * @return
     */
     TaskEntity createSubTask(TaskEntity ptask, String ptaskId, String assignee) {
        TaskEntity task = null;
        if (ptask != null) {
            //1.生成子任务
            task = (TaskEntity) taskService.newTask(UUIDGenerator.generate());
            task.setCategory(ptask.getCategory());
            task.setDescription(ptask.getDescription());
            task.setTenantId(ptask.getTenantId());
            task.setAssignee(assignee);
            task.setName(ptask.getName());
            task.setParentTaskId(ptaskId);
            task.setProcessDefinitionId(ptask.getProcessDefinitionId());
            task.setProcessInstanceId(ptask.getProcessInstanceId());
            task.setTaskDefinitionKey(ptask.getTaskDefinitionKey());
            task.setTaskDefinitionId(ptask.getTaskDefinitionId());
            task.setPriority(ptask.getPriority());
            task.setCreateTime(new Date());
            taskService.saveTask(task);
        }
        return task;
    }

    /**
     * add by nbacheng
     * 转办任务,兼容自定义业务
     * 转办就是直接将办理人assignee 换成别人，这时任务的拥有着不再是转办人，而是为空，相当与将任务转出，完成后继续下面的流程。
     * @param flowTaskVo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTask(FlowTaskVo flowTaskVo) {
    	if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
            throw new CustomException("任务处于挂起状态");
        }
    	SysUser loginUser = iFlowThirdService.getLoginUser();
    	SysUser targetUser = iFlowThirdService.getUserByUsername(flowTaskVo.getAssignee());
    	// 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        String assigncomment;
        if( task.getAssignee() != null ) {
        	SysUser oldUser = iFlowThirdService.getUserByUsername(task.getAssignee());
            assigncomment = oldUser.getRealname() + "经由"+ loginUser.getRealname() + "转办给" + targetUser.getRealname() + "意见:";
        }
        else {
        	assigncomment =  "经由"+ loginUser.getRealname() + "转办给" + targetUser.getRealname() + "意见:";
        }
       
    	if(StrUtil.isNotBlank(flowTaskVo.getDataId())){
    		
    		taskService.addComment(flowTaskVo.getTaskId(), flowTaskVo.getInstanceId(), FlowComment.ASSIGN.getType(),assigncomment);
    		taskService.setVariable(flowTaskVo.getTaskId(), "assign", assigncomment);
    		taskService.setAssignee(flowTaskVo.getTaskId(),flowTaskVo.getAssignee());
    		/*======================退回  回调以及关键数据保存======================*/
            //业务数据id
            String dataId = flowTaskVo.getDataId();
            if (dataId==null) return;
            //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
            FlowMyBusiness business = flowMyBusinessService.getByDataId(dataId);
            //**获取当前节点
            Task currentTask = taskService.createTaskQuery().processInstanceId(business.getProcessInstanceId()).active().singleResult();
            
            //spring容器类名
            String serviceImplName = business.getServiceImplName();
            FlowCallBackServiceI flowCallBackService = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplName);
            
            String doneUsers = business.getDoneUsers();
            // 处理过流程的人
            JSONArray doneUserList = new JSONArray();
            if (StrUtil.isNotBlank(doneUsers)){
                doneUserList = JSON.parseArray(doneUsers);
            }
            if (!doneUserList.contains(loginUser.getUsername())){
                doneUserList.add(loginUser.getUsername());
            }
       
            business.setActStatus(ActStatus.assign)
                    .setTaskId(currentTask.getId())
                    .setTaskNameId(currentTask.getTaskDefinitionKey())
                    .setTaskName(currentTask.getName())
                    .setDoneUsers(doneUserList.toJSONString())
                    .setTodoUsers(targetUser.getRealname())
            ;

            flowMyBusinessService.updateById(business);
            // 流程处理完后，进行回调业务层
            business.setValues(flowTaskVo.getValues());
            if (flowCallBackService!=null) flowCallBackService.afterFlowHandle(business);
    	}
    	else {
    		taskService.addComment(flowTaskVo.getTaskId(), flowTaskVo.getInstanceId(), FlowComment.ASSIGN.getType(), assigncomment);
    		taskService.setVariable(flowTaskVo.getTaskId(), "assign", assigncomment);
    		taskService.setAssignee(flowTaskVo.getTaskId(),flowTaskVo.getAssignee());
    	}
        
    }
    
    /**
     * 所有流程任务
     *        by nbacheng
     * @param pageNum
     * @param pageSize
     * @param flowTaskDto
     * @return
     * @throws  
     */
    @Override
    public Result allProcess(Integer pageNo, Integer pageSize, FlowTaskDto flowTaskDto)  {
        Page<FlowTaskDto> page = new Page<>();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime()
                .desc();
        /*=====参数=====*/
        if (StrUtil.isNotBlank(flowTaskDto.getProcDefName())){
        	historicProcessInstanceQuery.processDefinitionName(flowTaskDto.getProcDefName());
        }
        if (Objects.nonNull(flowTaskDto.getCreateTime())){
			historicProcessInstanceQuery.startedAfter(flowTaskDto.getCreateTime());
        }
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage((pageNo - 1)*pageSize, pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTaskDto> flowList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTaskDto flowTask = new FlowTaskDto();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());
            flowTask.setBusinessKey(hisIns.getBusinessKey());
            
            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            } else {
                long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            }
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setCategory(pd.getCategory());

            // 当前所处流程 todo: 本地启动放开以下注释
            
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollectionUtils.isNotEmpty(taskList)) {
                flowTask.setTaskId(taskList.get(0).getId());
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                flowTask.setTaskId(historicTaskInstance.get(0).getId());
            }
            //当然任务节点信息
            Map<String, Object> map = currentFlowRecord(hisIns.getId());
            if (Objects.nonNull(map)) {
            	if(map.containsKey("assigneeName")) flowTask.setAssigneeName(map.get("assigneeName").toString());
            	if(map.containsKey("deptName")) flowTask.setDeptName(map.get("deptName").toString());
            	if(map.containsKey("deptName"))flowTask.setTaskName(map.get("taskName").toString());
            }

            //添加发起人信息
            SysUser startUser = iFlowThirdService.getUserByUsername(hisIns.getStartUserId());
            List<String> departNamesByUsername = iFlowThirdService.getDepartNamesByUsername(hisIns.getStartUserId());
            flowTask.setStartUserId(startUser.getUsername());
            flowTask.setStartUserName(startUser.getRealname());
            flowTask.setStartDeptName(CollUtil.join(departNamesByUsername,"，"));
            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return Result.OK(page);
    }

    /**
     * 我发起的流程
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Result myProcess(Integer pageNum, Integer pageSize) {
        Page<FlowTaskDto> page = new Page<>();
        String username = iFlowThirdService.getLoginUser().getUsername();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .startedBy(username)
                .orderByProcessInstanceStartTime()
                .desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage((pageNum - 1)*pageSize, pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTaskDto> flowList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTaskDto flowTask = new FlowTaskDto();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());

            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            } else {
                long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            }
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setCategory(pd.getCategory());
            flowTask.setProcDefVersion(pd.getVersion());
            // 当前所处流程 todo: 本地启动放开以下注释
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollectionUtils.isNotEmpty(taskList)) {
                flowTask.setTaskId(taskList.get(0).getId());
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                flowTask.setTaskId(historicTaskInstance.get(0).getId());
            }
            //当然任务节点信息
            Map<String, Object> map = currentFlowRecord(hisIns.getId());
            if (Objects.nonNull(map)) {
            	if(map.containsKey("assigneeName")) flowTask.setAssigneeName(map.get("assigneeName").toString());
            	if(map.containsKey("deptName")) flowTask.setDeptName(map.get("deptName").toString());
            	if(map.containsKey("deptName"))flowTask.setTaskName(map.get("taskName").toString());
            }
           
            
            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return Result.OK(page);
    }

    /**
     * 我发起的流程
     *        by nbacheng
     * @param pageNum
     * @param pageSize
     * @return
     * @throws  
     */
    @Override
    public Result myProcessNew(Integer pageNo, Integer pageSize, FlowTaskDto flowTaskDto)  {
        Page<FlowTaskDto> page = new Page<>();
        String username = iFlowThirdService.getLoginUser().getUsername();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .startedBy(username)
                .orderByProcessInstanceStartTime()
                .desc();
        /*=====查询参数=====*/
        if (StrUtil.isNotBlank(flowTaskDto.getProcDefName())){
        	historicProcessInstanceQuery.processDefinitionName(flowTaskDto.getProcDefName());
        }
        if (Objects.nonNull(flowTaskDto.getCreateTime())){
			historicProcessInstanceQuery.startedAfter(flowTaskDto.getCreateTime());
        }
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage((pageNo - 1)*pageSize, pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTaskDto> flowList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTaskDto flowTask = new FlowTaskDto();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());
            flowTask.setBusinessKey(hisIns.getBusinessKey());
            
            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            } else {
                long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            }
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setCategory(pd.getCategory());
            flowTask.setProcDefVersion(pd.getVersion());
            // 当前所处流程 todo: 本地启动放开以下注释
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollectionUtils.isNotEmpty(taskList)) {
                flowTask.setTaskId(taskList.get(0).getId());
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                flowTask.setTaskId(historicTaskInstance.get(0).getId());
            }
            //当然任务节点信息
            Map<String, Object> map = currentFlowRecord(hisIns.getId());
            if (Objects.nonNull(map)) {
            	if(map.containsKey("assigneeName")) flowTask.setAssigneeName(map.get("assigneeName").toString());
            	if(map.containsKey("deptName")) flowTask.setDeptName(map.get("deptName").toString());
            	if(map.containsKey("deptName"))flowTask.setTaskName(map.get("taskName").toString());
            }
           
            
            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return Result.OK(page);
    }
    
    /**
     * 流程历史当前审批节点信息
     * add by nbacheng
     * @param  procInsId 流程实例Id,
     * @return
     */
    @Override
    public Map<String, Object> currentFlowRecord(String procInsId) {
    	Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(procInsId)) {

            Task task = taskService.createTaskQuery().processInstanceId(procInsId).singleResult();
            if(Objects.nonNull(task)) {
            	if (StringUtils.isNotBlank(task.getAssignee())) {
            	  SysUser sysUser = iFlowThirdService.getUserByUsername(task.getAssignee());
                  map.put("assigneeName", sysUser.getRealname());
                  List<String> departNamesByUsername = iFlowThirdService.getDepartNamesByUsername(task.getAssignee());
                  map.put("deptName", CollUtil.join(departNamesByUsername,"，"));
                  map.put("taskName", task.getName());
              }
            }
            
        }
        return map;
    }
    
    /**
     * 取消申请
     *
     * @param flowTaskVo
     * @return
     */
    @Override
    public Result stopProcess(FlowTaskVo flowTaskVo) {
        List<Task> task = taskService.createTaskQuery().processInstanceId(flowTaskVo.getInstanceId()).list();
        if (CollectionUtils.isEmpty(task)) {
            throw new CustomException("流程未启动或已执行完成，取消申请失败");
        }

        SysUser loginUser = iFlowThirdService.getLoginUser();
        ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery().processInstanceId(flowTaskVo.getInstanceId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        if (Objects.nonNull(bpmnModel)) {
            Process process = bpmnModel.getMainProcess();
            List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
            if (CollectionUtils.isNotEmpty(endNodes)) {
                Authentication.setAuthenticatedUserId(loginUser.getUsername());
                taskService.addComment(task.get(0).getId(), processInstance.getProcessInstanceId(), FlowComment.STOP.getType(),
                        StringUtils.isBlank(flowTaskVo.getComment()) ? "取消申请" : flowTaskVo.getComment());
                String endId = endNodes.get(0).getId();
                List<Execution> executions =
                        runtimeService.createExecutionQuery().parentId(processInstance.getProcessInstanceId()).list();
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
                runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds,
                        endId).changeState();
            }
        }

        return Result.OK("取消审批成功");
    }

    /**
     * 撤回流程  todo 目前存在错误
     *
     * @param flowTaskVo
     * @return
     */
    @Override
    public Result revokeProcess(FlowTaskVo flowTaskVo) {
        Task task = taskService.createTaskQuery().processInstanceId(flowTaskVo.getInstanceId()).singleResult();
        if (task == null) {
            throw new CustomException("流程未启动或已执行完成，无法撤回");
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .orderByTaskCreateTime()
                .asc()
                .list();
        String myTaskId = null;
        HistoricTaskInstance myTask = null;
        for (HistoricTaskInstance hti : htiList) {
            if (loginUser.getUsername().toString().equals(hti.getAssignee())) {
                myTaskId = hti.getId();
                myTask = hti;
                break;
            }
        }
        if (null == myTaskId) {
            throw new CustomException("该任务非当前用户提交，无法撤回");
        }

        String processDefinitionId = myTask.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //变量
//      Map<String, VariableInstance> variables = runtimeService.getVariableInstances(currentTask.getExecutionId());
        String myActivityId = null;
        List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
                .executionId(myTask.getExecutionId()).finished().list();
        for (HistoricActivityInstance hai : haiList) {
            if (myTaskId.equals(hai.getTaskId())) {
                myActivityId = hai.getActivityId();
                break;
            }
        }
        FlowNode myFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(myActivityId);

        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);

        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<>(flowNode.getOutgoingFlows());


        return Result.OK("撤回流程成功");
    }

    /**
     * 代办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return
     */
    @Override
    public Result todoList(Integer pageNum, Integer pageSize) {
        Page<FlowTaskDto> page = new Page<>();
        String username = iFlowThirdService.getLoginUser().getUsername();
        TaskQuery taskQuery = taskService.createTaskQuery()
                .active()
                .includeProcessVariables()
                .taskAssignee(username)
                .orderByTaskCreateTime().desc();
        page.setTotal(taskQuery.count());
        List<Task> taskList = taskQuery.listPage((pageNum - 1)*pageSize, pageSize);
        List<FlowTaskDto> flowList = new ArrayList<>();
        for (Task task : taskList) {
            FlowTaskDto flowTask = new FlowTaskDto();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            flowTask.setBusinessKey(historicProcessInstance.getBusinessKey());
            
            SysUser startUser = iFlowThirdService.getUserByUsername(historicProcessInstance.getStartUserId());
            List<String> departNamesByUsername = iFlowThirdService.getDepartNamesByUsername(historicProcessInstance.getStartUserId());
            flowTask.setStartUserId(startUser.getUsername());
            flowTask.setStartUserName(startUser.getRealname());
            flowTask.setStartDeptName(CollUtil.join(departNamesByUsername,"，"));
            flowList.add(flowTask);
        }

        page.setRecords(flowList);
        return Result.OK(page);
    }


    /**
     * 代办任务列表
     *  add by nbacheng
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param FlowTaskDto flowTaskDto
     * @return
     */
    @Override
    public Result todoListNew(Integer pageNo, Integer pageSize, FlowTaskDto flowTaskDto) {
        Page<FlowTaskDto> page = new Page<>();
        String username = iFlowThirdService.getLoginUser().getUsername();
        TaskQuery taskQuery = taskService.createTaskQuery()
                .active()
                .includeProcessVariables()
                .taskAssignee(username)
                .orderByTaskCreateTime().desc();
        /*=====查询参数=====*/
        if (StrUtil.isNotBlank(flowTaskDto.getProcDefName())){
        	taskQuery = taskQuery.processDefinitionNameLike("%"+flowTaskDto.getProcDefName()+"%");
        }
        if (Objects.nonNull(flowTaskDto.getCreateTime())){
        	taskQuery = taskQuery.taskCreatedAfter(flowTaskDto.getCreateTime());
        }
        page.setTotal(taskQuery.count());
        List<Task> taskList = taskQuery.listPage((pageNo - 1)*pageSize, pageSize);
        List<FlowTaskDto> flowList = new ArrayList<>();
        for (Task task : taskList) {
            FlowTaskDto flowTask = new FlowTaskDto();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
           
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setCategory(pd.getCategory());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            flowTask.setBusinessKey(historicProcessInstance.getBusinessKey());
            
            SysUser startUser = iFlowThirdService.getUserByUsername(historicProcessInstance.getStartUserId());
            List<String> departNamesByUsername = iFlowThirdService.getDepartNamesByUsername(historicProcessInstance.getStartUserId());
            flowTask.setStartUserId(startUser.getUsername());
            flowTask.setStartUserName(startUser.getRealname());
            flowTask.setStartDeptName(CollUtil.join(departNamesByUsername,"，"));
            flowList.add(flowTask);
        }

        page.setRecords(flowList);
        return Result.OK(page);
    }
    
    /**
     * 已办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return
     */
    @Override
    public Result finishedList(Integer pageNum, Integer pageSize) {
        Page<FlowTaskDto> page = new Page<>();
        String username = iFlowThirdService.getLoginUser().getUsername();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .includeProcessVariables()
                .finished()
                .taskAssignee(username)
                .orderByHistoricTaskInstanceEndTime()
                .desc();
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.listPage((pageNum - 1)*pageSize, pageSize);
        List<FlowTaskDto> hisTaskList = Lists.newArrayList();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            FlowTaskDto flowTask = new FlowTaskDto();
            // 当前流程信息
            flowTask.setTaskId(histTask.getId());
            // 审批人员信息
            flowTask.setCreateTime(histTask.getCreateTime());
            flowTask.setFinishTime(histTask.getEndTime());
            flowTask.setDuration(getDate(histTask.getDurationInMillis()));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());

            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(histTask.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(histTask.getProcessInstanceId());
            flowTask.setHisProcInsId(histTask.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(histTask.getProcessInstanceId())
                    .singleResult();
            
            flowTask.setBusinessKey(historicProcessInstance.getBusinessKey());
            SysUser startUser = iFlowThirdService.getUserByUsername(historicProcessInstance.getStartUserId());
            flowTask.setStartUserId(startUser.getUsername());
            flowTask.setStartUserName(startUser.getRealname());
            List<String> departNamesByUsername = iFlowThirdService.getDepartNamesByUsername(historicProcessInstance.getStartUserId());
            flowTask.setStartDeptName(CollUtil.join(departNamesByUsername,"，"));
            hisTaskList.add(flowTask);
        }
        page.setTotal(hisTaskList.size());
        page.setRecords(hisTaskList);
//        Map<String, Object> result = new HashMap<>();
//        result.put("result",page);
//        result.put("finished",true);
        return Result.OK(page);
    }
    
    /**
     * 已办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param
     * @return
     */
    @Override
    public Result finishedListNew(Integer pageNo, Integer pageSize, FlowTaskDto flowTaskDto) {
        Page<FlowTaskDto> page = new Page<>();
        String username = iFlowThirdService.getLoginUser().getUsername();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .includeProcessVariables()
                .finished()
                .taskAssignee(username)
                .orderByHistoricTaskInstanceEndTime()
                .desc();
        /*=====查询参数=====*/
        if (StrUtil.isNotBlank(flowTaskDto.getProcDefName())){
        	taskInstanceQuery = taskInstanceQuery.processDefinitionNameLike("%"+flowTaskDto.getProcDefName()+"%");
        }
        if (Objects.nonNull(flowTaskDto.getCreateTime())){
        	taskInstanceQuery = taskInstanceQuery.taskCreatedAfter(flowTaskDto.getCreateTime());
        }
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.listPage((pageNo - 1)*pageSize, pageSize);
        List<FlowTaskDto> hisTaskList = Lists.newArrayList();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            FlowTaskDto flowTask = new FlowTaskDto();
            // 当前流程信息
            flowTask.setTaskId(histTask.getId());
            // 审批人员信息
            flowTask.setCreateTime(histTask.getCreateTime());
            flowTask.setFinishTime(histTask.getEndTime());
            flowTask.setDuration(getDate(histTask.getDurationInMillis()));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());

            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(histTask.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setCategory(pd.getCategory());
            flowTask.setProcInsId(histTask.getProcessInstanceId());
            flowTask.setHisProcInsId(histTask.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(histTask.getProcessInstanceId())
                    .singleResult();
            
            flowTask.setBusinessKey(historicProcessInstance.getBusinessKey());
            SysUser startUser = iFlowThirdService.getUserByUsername(historicProcessInstance.getStartUserId());
            flowTask.setStartUserId(startUser.getUsername());
            flowTask.setStartUserName(startUser.getRealname());
            List<String> departNamesByUsername = iFlowThirdService.getDepartNamesByUsername(historicProcessInstance.getStartUserId());
            flowTask.setStartDeptName(CollUtil.join(departNamesByUsername,"，"));
            hisTaskList.add(flowTask);
        }
        page.setTotal(hisTaskList.size());
        page.setRecords(hisTaskList);
//        Map<String, Object> result = new HashMap<>();
//        result.put("result",page);
//        result.put("finished",true);
        return Result.OK(page);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * 流程历史流转记录
     *
     * @param dataId 业务数据Id
     * @return
     */
    @Override
    public Result flowRecordBydataid(String dataId) {
        FlowMyBusiness business = flowMyBusinessService.getByDataId(dataId);
        String procInsId = business.getProcessInstanceId();
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(procInsId)) {
            List<HistoricActivityInstance> list = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(procInsId)
                    .orderByHistoricActivityInstanceStartTime()
                    .desc().list();
            List<FlowTaskDto> hisFlowList = new ArrayList<>();
            for (HistoricActivityInstance histIns : list) {
                if (StringUtils.isNotBlank(histIns.getTaskId())) {
                    FlowTaskDto flowTask = new FlowTaskDto();
                    flowTask.setTaskId(histIns.getTaskId());
                    flowTask.setTaskName(histIns.getActivityName());
                    flowTask.setTaskDefKey(histIns.getActivityId());
                    flowTask.setCreateTime(histIns.getStartTime());
                    flowTask.setFinishTime(histIns.getEndTime());
                    if (StringUtils.isNotBlank(histIns.getAssignee())) {
                        SysUser sysUser = iFlowThirdService.getUserByUsername(histIns.getAssignee());
                        flowTask.setAssigneeId(sysUser.getUsername());
                        flowTask.setAssigneeName(sysUser.getRealname());
                        List<String> departNamesByUsername = iFlowThirdService.getDepartNamesByUsername(histIns.getAssignee());
                        flowTask.setDeptName(CollUtil.join(departNamesByUsername,"，"));
                        if (StrUtil.equals(histIns.getActivityId(),ProcessConstants.START_NODE)){
                        //    开始节点，把候选人设置为发起人，这个值已被其他地方设置过，与实际办理人一致即可
                            flowTask.setCandidate(sysUser.getRealname());
                        }
                    }
                    // 展示审批人员
                    List<HistoricIdentityLink> linksForTask = historyService.getHistoricIdentityLinksForTask(histIns.getTaskId());
                    StringBuilder stringBuilder = new StringBuilder();
                    for (HistoricIdentityLink identityLink : linksForTask) {
                        if (IdentityLinkType.CANDIDATE.equals(identityLink.getType())) {
                            if (StringUtils.isNotBlank(identityLink.getUserId())) {
                                SysUser sysUser = iFlowThirdService.getUserByUsername(identityLink.getUserId());
                                stringBuilder.append(sysUser.getRealname()).append(",");
                            }
                            /*已经全部设置到 CANDIDATE 了，不拿组了*/
                            /*if (StringUtils.isNotBlank(identityLink.getGroupId())) {
                                List<SysRole> allRole = iFlowThirdService.getAllRole();
                                SysRole sysRole = allRole.stream().filter(o -> StringUtils.equals(identityLink.getGroupId(), o.getId())).findAny().orElse(new SysRole());
                                stringBuilder.append(sysRole.getRoleName()).append(",");
                            }*/
                        }
                    }
                    if (StringUtils.isNotBlank(stringBuilder)) {
                        flowTask.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
                    }

                    flowTask.setDuration(histIns.getDurationInMillis() == null || histIns.getDurationInMillis() == 0 ? null : getDate(histIns.getDurationInMillis()));
                    // 获取意见评论内容
                    List<Comment> commentList = taskService.getProcessInstanceComments(histIns.getProcessInstanceId());
                    List<FlowCommentDto> listFlowCommentDto = new ArrayList<>();
                    commentList.forEach(comment -> {
                        if (histIns.getTaskId().equals(comment.getTaskId())) {
                            //flowTask.setComment(FlowCommentDto.builder().type(comment.getType()).comment(comment.getFullMessage()).build());
                        	//FlowCommentDto flowcommentDto = FlowCommentDto.builder().type(comment.getType()).comment(comment.getFullMessage()).build();
                        	FlowCommentDto flowcommentDto = new FlowCommentDto();
                        	flowcommentDto.setType(comment.getType());
                        	flowcommentDto.setComment(comment.getFullMessage());
                            listFlowCommentDto.add(flowcommentDto);
                        }
                    });
                    flowTask.setListFlowCommentDto(listFlowCommentDto); 
                    hisFlowList.add(flowTask);
                }
            }
            map.put("flowList", hisFlowList);
        }
        // 获取初始化表单
        String serviceImplName = business.getServiceImplName();
        FlowCallBackServiceI flowCallBackService = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplName);
        // 流程处理完后，进行回调业务层
        if (flowCallBackService!=null){
            Object businessDataById = flowCallBackService.getBusinessDataById(dataId);
            map.put("formData",businessDataById);
        }
        return Result.OK(map);
    }
    
    /**
     * 流程历史流转记录
     * add by nbacheng
     * @param  procInsId 流程实例Id, 流程发布id
     * @return
     */
    @Override
    public Result flowRecord(String procInsId,String deployId, String businessKey) {
    	Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(procInsId)) {
            List<HistoricActivityInstance> list = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(procInsId)
                    .orderByHistoricActivityInstanceStartTime()
                    .desc().list();
            List<FlowTaskDto> hisFlowList = new ArrayList<>();
            for (HistoricActivityInstance histIns : list) {
                if (StringUtils.isNotBlank(histIns.getTaskId())) {
                    FlowTaskDto flowTask = new FlowTaskDto();
                    flowTask.setTaskId(histIns.getTaskId());
                    flowTask.setTaskName(histIns.getActivityName());
                    flowTask.setCreateTime(histIns.getStartTime());
                    flowTask.setFinishTime(histIns.getEndTime());
                    if (StringUtils.isNotBlank(histIns.getAssignee())) {
                    	SysUser sysUser = iFlowThirdService.getUserByUsername(histIns.getAssignee());
                        flowTask.setAssigneeId(sysUser.getUsername());
                        flowTask.setAssigneeName(sysUser.getRealname());
                        List<String> departNamesByUsername = iFlowThirdService.getDepartNamesByUsername(histIns.getAssignee());
                        flowTask.setDeptName(CollUtil.join(departNamesByUsername,"，"));
                    }
                    // 展示审批人员
                    List<HistoricIdentityLink> linksForTask = historyService.getHistoricIdentityLinksForTask(histIns.getTaskId());
                    StringBuilder stringBuilder = new StringBuilder();
                    for (HistoricIdentityLink identityLink : linksForTask) {
                        if ("candidate".equals(identityLink.getType())) {
                            if (StringUtils.isNotBlank(identityLink.getUserId())) {
                            	SysUser sysUser = iFlowThirdService.getUserByUsername(histIns.getAssignee());
                                stringBuilder.append(sysUser.getRealname()).append(",");
                            }
                            if (StringUtils.isNotBlank(identityLink.getGroupId())) {
                            	 List<SysRole> allRole = iFlowThirdService.getAllRole();
                                 SysRole sysRole = allRole.stream().filter(o -> StringUtils.equals(identityLink.getGroupId(), o.getId())).findAny().orElse(new SysRole());
                                stringBuilder.append(sysRole.getRoleName()).append(",");
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(stringBuilder)) {
                        flowTask.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
                    }
                    
                    flowTask.setDuration(histIns.getDurationInMillis() == null || histIns.getDurationInMillis() == 0 ? null : getDate(histIns.getDurationInMillis()));
                    // 获取意见评论内容
                    List<Comment> commentList = taskService.getProcessInstanceComments(histIns.getProcessInstanceId());
                    List<FlowCommentDto> listFlowCommentDto = new ArrayList<FlowCommentDto>();
                    commentList.forEach(comment -> {
                        if (histIns.getTaskId().equals(comment.getTaskId())) {
                            //flowTask.setComment(FlowCommentDto.builder().type(comment.getType()).comment(comment.getFullMessage()).build());
                            //FlowCommentDto flowcommentDto = FlowCommentDto.builder().type(comment.getType()).comment(comment.getFullMessage()).build();
                        	FlowCommentDto flowcommentDto = new FlowCommentDto();
                        	flowcommentDto.setType(comment.getType());
                        	flowcommentDto.setComment(comment.getFullMessage());
                            listFlowCommentDto.add(flowcommentDto);
                        }
                    });
                    flowTask.setListFlowCommentDto(listFlowCommentDto); 
                    //获取附件
                    List<Attachment> commentfileList = taskService.getProcessInstanceAttachments(histIns.getProcessInstanceId());
                    List<FlowCommentFileDto> listcommentFileDto =  new ArrayList<FlowCommentFileDto>();
                    commentfileList.forEach(commentfile -> {
                        if (histIns.getTaskId().equals(commentfile.getTaskId())) {
                        	FlowCommentFileDto flowcommenfiletDto = new FlowCommentFileDto();
                        	flowcommenfiletDto.setType(commentfile.getType());
                        	flowcommenfiletDto.setFileurl(commentfile.getUrl());
                        	listcommentFileDto.add(flowcommenfiletDto);
                        }
                    });
                    flowTask.setListcommentFileDto(listcommentFileDto);
                    hisFlowList.add(flowTask);
                }
            }
            map.put("flowList", hisFlowList);
//            // 查询当前任务是否完成
//            List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
//            if (CollectionUtils.isNotEmpty(taskList)) {
//                map.put("finished", true);
//            } else {
//                map.put("finished", false);
//            }
        }
     // 获取初始化自定义表单
        if(StringUtils.isNotBlank(businessKey)) {
          FlowMyBusiness business = flowMyBusinessService.getByDataId(businessKey);
          String serviceImplName = business.getServiceImplName();
          FlowCallBackServiceI flowCallBackService = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplName);
          // 流程处理完后，进行回调业务层
          if (flowCallBackService!=null){
            Object businessDataById = flowCallBackService.getBusinessDataById(businessKey);
            map.put("formData",businessDataById);
            map.put("routeName", business.getRouteName());
            
          }
        }
        else {
	        // 第一次申请获取初始化表单
	        if (StringUtils.isNotBlank(deployId)) {
	            SysForm sysForm = sysInstanceFormService.selectSysDeployFormByDeployId(deployId);
	            if (Objects.isNull(sysForm)) {
	                return Result.error("请先配置流程表单");
	            }
	            map.put("formData", JSONObject.parseObject(sysForm.getFormContent()));
	        }
        }
        
        return Result.OK(map);
    }

    /**
     * 根据任务ID查询挂载的表单信息
     *
     * @param taskId 任务Id
     * @return
     */
    @Override
    public Task getTaskForm(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return task;
    }

    /**
     * 获取流程过程图
     *
     * @param processId
     * @return
     */
    @Override
    public InputStream diagram(String processId) {
        String processDefinitionId;
        // 获取当前的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        // 如果流程已经结束，则得到结束节点
        if (Objects.isNull(processInstance)) {
            HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();

            processDefinitionId = pi.getProcessDefinitionId();
        } else {// 如果流程没有结束，则取当前活动节点
            // 根据流程实例ID获得当前处于活动状态的ActivityId合集
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
            processDefinitionId = pi.getProcessDefinitionId();
        }

        // 获得活动的节点
        List<HistoricActivityInstance> highLightedFlowList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).orderByHistoricActivityInstanceStartTime().asc().list();

        List<String> highLightedFlows = new ArrayList<>();
        List<String> highLightedNodes = new ArrayList<>();
        //高亮线
        for (HistoricActivityInstance tempActivity : highLightedFlowList) {
            if ("sequenceFlow".equals(tempActivity.getActivityType())) {
                //高亮线
                highLightedFlows.add(tempActivity.getActivityId());
            } else {
                //高亮节点
                highLightedNodes.add(tempActivity.getActivityId());
            }
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
        //获取自定义图片生成器
        ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedNodes, highLightedFlows, configuration.getActivityFontName(),
                configuration.getLabelFontName(), configuration.getAnnotationFontName(), configuration.getClassLoader(), 1.0, true);
        return in;

    }

    /**
     * 获取流程执行过程
     *
     * @param procInsId
     * @return
     */
    @Override
    public Result getFlowViewer(String procInsId) {
        List<FlowViewerDto> flowViewerList = new ArrayList<>();
        FlowViewerDto flowViewerDto;
        // 获得活动的节点
        List<HistoricActivityInstance> hisActIns = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInsId)
                .orderByHistoricActivityInstanceStartTime()
                .asc().list();
        for (HistoricActivityInstance activityInstance : hisActIns) {
            if (!"sequenceFlow".equals(activityInstance.getActivityType())) {
                flowViewerDto = new FlowViewerDto();
                flowViewerDto.setKey(activityInstance.getActivityId());
                flowViewerDto.setCompleted(!Objects.isNull(activityInstance.getEndTime()));
                flowViewerList.add(flowViewerDto);
            }
        }
        return Result.OK(flowViewerList);
    }

    @Override
    public Result getFlowViewerByDataId(String dataId) {
        LambdaQueryWrapper<FlowMyBusiness> flowMyBusinessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flowMyBusinessLambdaQueryWrapper.eq(FlowMyBusiness::getDataId,dataId)
        ;
        //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
        FlowMyBusiness business = flowMyBusinessService.getOne(flowMyBusinessLambdaQueryWrapper);
        return this.getFlowViewer(business.getProcessInstanceId());
    }
    
    /**
     * 获取流程执行过程
     *
     * @param processDefinitionName
     * @return
     */
    @Override
    public Result getFlowViewerByName(String processDefinitionName) {
        List<FlowViewerDto> flowViewerList = new ArrayList<>();
        FlowViewerDto flowViewerDto;
        // 获得活动的节点
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        String processId;
        if(processDefinitionQuery.processDefinitionName(processDefinitionName).processDefinitionCategory(Category.ddxz.name())
        		           .latestVersion().active().list().size() > 0) {
        	processId = processDefinitionQuery.processDefinitionName(processDefinitionName)
        		           .latestVersion().active().list().get(0).getId();
	        List<HistoricActivityInstance> hisActIns = historyService.createHistoricActivityInstanceQuery()
	                .processDefinitionId(processId)
	                .orderByHistoricActivityInstanceStartTime()
	                .asc().list();
	        for (HistoricActivityInstance activityInstance : hisActIns) {
	            if (!"sequenceFlow".equals(activityInstance.getActivityType())) {
	                flowViewerDto = new FlowViewerDto();
	                flowViewerDto.setKey(activityInstance.getActivityId());
	                flowViewerDto.setCompleted(!Objects.isNull(activityInstance.getEndTime()));
	                flowViewerList.add(flowViewerDto);
	            }
	        }
	        return Result.OK(flowViewerList);
        }
        else {
        	return Result.OK(null);
        }
    }
    

    /**
     * 获取流程变量
     *
     * @param taskId
     * @return
     */
    @Override
    public Result processVariables(String taskId) {
        // 流程变量
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return Result.OK(historicTaskInstance.getProcessVariables());
        } else {
            Map<String, Object> variables = taskService.getVariables(taskId);
            return Result.OK(variables);
        }
    }

    /**
     * 获取下一节点
     *
     * @param flowTaskVo 任务
     * @return
     */
    @Override
    public Result getNextFlowNode(FlowTaskVo flowTaskVo) {
        // todo 目前只支持部分功能
        FlowNextDto flowNextDto = this.getNextFlowNode(flowTaskVo.getTaskId(), flowTaskVo.getValues());
        if (flowNextDto==null) {
            return Result.OK("流程已完结", null);
        }
        return Result.OK(flowNextDto);
    
    }

    /**  modify by nbacheng
     * 获取下一个节点信息,流程定义上的节点信息
     * @param taskId 当前节点id
     * @param values 流程变量
     * @return 如果返回null，表示没有下一个节点，流程结束
     */
  
    public FlowNextDto getNextFlowNode(String taskId, Map<String, Object> values) {   
    	//当前节点
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        FlowNextDto flowNextDto = new FlowNextDto();
        
    	if (Objects.nonNull(task)) {
        	// 下个任务节点
    		if (DelegationState.PENDING.equals(task.getDelegationState())) { //对于委派的处理 
	        	List<UserTask> nextUserTask = FindNextNodeUtil.getNextUserTasks(repositoryService, task, values);
	            if (CollectionUtils.isNotEmpty(nextUserTask)) {
	            	flowNextDto.setType(ProcessConstants.FIXED);//委派是按原来流程执行，所以直接赋值返回
	            	return flowNextDto;
	            }
	            else {
	            	return null;
	            }
        	
             }
            List<UserTask> nextUserTask = FindNextNodeUtil.getNextUserTasks(repositoryService, task, values);
            if (CollectionUtils.isNotEmpty(nextUserTask)) {
                for (UserTask userTask : nextUserTask) {
                    MultiInstanceLoopCharacteristics multiInstance = userTask.getLoopCharacteristics();
                    // 会签节点
                    if (Objects.nonNull(multiInstance)) {
                        List<SysUser> list = iFlowThirdService.getAllUser();

                        flowNextDto.setVars(ProcessConstants.PROCESS_MULTI_INSTANCE_USER);
                        flowNextDto.setType(ProcessConstants.PROCESS_MULTI_INSTANCE);
                        flowNextDto.setUserList(list);
                        flowNextDto.setUserTask(userTask);
                    } else {

                        // 读取自定义节点属性 判断是否是否需要动态指定任务接收人员、组,目前只支持用户角色或多用户，还不支持子流程和变量
                        //String dataType = userTask.getAttributeValue(ProcessConstants.NAMASPASE, ProcessConstants.PROCESS_CUSTOM_DATA_TYPE);
                        //String userType = userTask.getAttributeValue(ProcessConstants.NAMASPASE, ProcessConstants.PROCESS_CUSTOM_USER_TYPE);

                        List<String> rolelist = new ArrayList<>();
                        rolelist = userTask.getCandidateGroups();
                        List<String> userlist = new ArrayList<>();
                        userlist = userTask.getCandidateUsers();
                        String assignee = userTask.getAssignee();
                        // 处理加载动态指定下一节点接收人员信息
                        if(assignee !=null) {
                        	List<SysUser> list = new ArrayList<SysUser>();
                        	SysUser sysUser =  iFlowThirdService.getUserByUsername(assignee);
                    		List<String> listdepname = iFlowThirdService.getDepartNamesByUsername(assignee);
                    		if(listdepname.size()>0){
                    			sysUser.setOrgCodeTxt(listdepname.get(0).toString());
                    		}	
                    		list.add(sysUser);
                        	flowNextDto.setVars(ProcessConstants.PROCESS_APPROVAL);
                        	flowNextDto.setType(ProcessConstants.USER_TYPE_ASSIGNEE);
                        	flowNextDto.setUserList(list);
                        	flowNextDto.setUserTask(userTask);
                        }
                        else if(rolelist.size() > 0) {
                        	List<SysUser> list = new ArrayList<SysUser>();
							for(String roleId : rolelist ){
                        	  List<SysUser> templist = iFlowThirdService.getUsersByRoleId(roleId);
                        	  for(SysUser sysuser : templist) {
                          		SysUser sysUserTemp = iFlowThirdService.getUserByUsername(sysuser.getUsername());
                          		List<String> listdepname = iFlowThirdService.getDepartNamesByUsername(sysuser.getUsername());
                          		if(listdepname.size()>0){
                          			sysUserTemp.setOrgCodeTxt(listdepname.get(0).toString());
                          		} 
                          		list.add(sysUserTemp);
                          	  }
                        	}
							flowNextDto.setVars(ProcessConstants.PROCESS_APPROVAL);
                            flowNextDto.setType(ProcessConstants.USER_TYPE_USERS);
                            flowNextDto.setUserList(list);
                            flowNextDto.setUserTask(userTask);
                        	
                        }
                        else if(userlist.size() > 0) {
                        	List<SysUser> list = new ArrayList<SysUser>();
                        	for(String username : userlist) {
                        		SysUser sysUser =  iFlowThirdService.getUserByUsername(username);
                        		List<String> listdepname = iFlowThirdService.getDepartNamesByUsername(username);
                        		if(listdepname.size()>0){
                        			sysUser.setOrgCodeTxt(listdepname.get(0).toString());
                        		}	
                        		list.add(sysUser);
                        	}
                        	flowNextDto.setVars(ProcessConstants.PROCESS_APPROVAL);
                            flowNextDto.setType(ProcessConstants.USER_TYPE_USERS);
                            flowNextDto.setUserList(list);
                            flowNextDto.setUserTask(userTask);
                        }
                        else {
                        	flowNextDto.setType(ProcessConstants.FIXED);
                        }
                    }
                }
                return flowNextDto;
            } else {
                return null;
          }
       }
       return null;

    }
    /*
    public FlowNextDto getNextFlowNode(String taskId, Map<String, Object> values) {
        //当前节点
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.nonNull(task)) {
            // 下个任务节点
            List<UserTask> nextUserTask = FindNextNodeUtil.getNextUserTasks(repositoryService, task, values);
            if (CollectionUtils.isNotEmpty(nextUserTask)) {
                FlowNextDto flowNextDto = new FlowNextDto();
                for (UserTask userTask : nextUserTask) {
                    flowNextDto.setUserTask(userTask);
                    //待办人员
                    List<SysUser> sysUserFromTask = this.getSysUserFromTask(userTask);
                    flowNextDto.setUserList(sysUserFromTask);
                    MultiInstanceLoopCharacteristics   multiInstance =  userTask.getLoopCharacteristics();
                    if (Objects.nonNull(multiInstance)) {
                    //  这里是否只有子流程才会这样?
                    	List<SysUser> list = iFlowThirdService.getAllUser();

                        flowNextDto.setVars(ProcessConstants.PROCESS_MULTI_INSTANCE_USER);
                        flowNextDto.setType(ProcessConstants.PROCESS_MULTI_INSTANCE);
                        flowNextDto.setUserList(list);
                        flowNextDto.setUserTask(userTask);
                    } else {
                        // todo  读取自定义节点属性 判断是否是否需要动态指定任务接收人员、组,目前只支持用户角色或多用户，还不支持子流程和变量
                        //String dataType = userTask.getAttributeValue(ProcessConstants.NAMASPASE, ProcessConstants.PROCESS_CUSTOM_DATA_TYPE);
                        //String userType = userTask.getAttributeValue(ProcessConstants.NAMASPASE, ProcessConstants.PROCESS_CUSTOM_USER_TYPE);
                    	if(sysUserFromTask.size()>1) {
                    		flowNextDto.setVars(ProcessConstants.PROCESS_APPROVAL);
                            flowNextDto.setType(ProcessConstants.USER_TYPE_USERS);
                            flowNextDto.setUserTask(userTask);
                    	}
                    	else {
                    		flowNextDto.setType(ProcessConstants.FIXED);
                    	}
                    }
                }
                return flowNextDto;
            }
        }
        return null;

    }*/
    public List<SysUser> getSysUserFromTask(UserTask userTask) {
        String assignee = userTask.getAssignee();
        if (StrUtil.isNotBlank(assignee)){
            // 指定单人
            SysUser userByUsername = iFlowThirdService.getUserByUsername(assignee);
            return Lists.newArrayList(userByUsername);
        }
        List<String> candidateUsers = userTask.getCandidateUsers();
        if (CollUtil.isNotEmpty(candidateUsers)){
            // 指定多人
            List<SysUser> list = iFlowThirdService.getAllUser();
            return list.stream().filter(o->candidateUsers.contains(o.getUsername())).collect(Collectors.toList());
        }
        List<String> candidateGroups = userTask.getCandidateGroups();
        if (CollUtil.isNotEmpty(candidateGroups)){
        //    指定多组
            List<SysUser> userList = Lists.newArrayList();
            for (String candidateGroup : candidateGroups) {
                List<SysUser> usersByRoleId = iFlowThirdService.getUsersByRoleId(candidateGroup);
                userList.addAll(usersByRoleId);
            }
            return userList;
        }
        return Lists.newArrayList();
    }
    /**
     * 流程完成时间处理
     *
     * @param ms
     * @return
     */
    private String getDate(long ms) {

        long day = ms / (24 * 60 * 60 * 1000);
        long hour = (ms / (60 * 60 * 1000) - day * 24);
        long minute = ((ms / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (ms / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);

        if (day > 0) {
            return day + "天" + hour + "小时" + minute + "分钟";
        }
        if (hour > 0) {
            return hour + "小时" + minute + "分钟";
        }
        if (minute > 0) {
            return minute + "分钟";
        }
        if (second > 0) {
            return second + "秒";
        } else {
            return 0 + "秒";
        }
    }
}
