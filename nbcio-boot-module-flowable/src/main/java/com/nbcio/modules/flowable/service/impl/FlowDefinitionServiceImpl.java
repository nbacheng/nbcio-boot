package com.nbcio.modules.flowable.service.impl;

import cn.hutool.core.util.StrUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.SpringContextUtils;
import com.nbcio.modules.flowable.apithird.business.entity.FlowMyBusiness;
import com.nbcio.modules.flowable.apithird.business.service.impl.FlowMyBusinessServiceImpl;
import com.nbcio.modules.flowable.apithird.entity.ActStatus;
import com.nbcio.modules.flowable.apithird.entity.SysUser;
import com.nbcio.modules.flowable.apithird.entity.FlowCategory.Category;
import com.nbcio.modules.flowable.apithird.service.FlowCallBackServiceI;
import com.nbcio.modules.flowable.apithird.service.FlowCommonService;
import com.nbcio.modules.flowable.apithird.service.IFlowThirdService;
import com.nbcio.modules.flowable.common.constant.ProcessConstants;
import com.nbcio.modules.flowable.common.enums.FlowComment;
import com.nbcio.modules.flowable.domain.dto.FlowNextDto;
import com.nbcio.modules.flowable.domain.dto.FlowProcDefDto;
import com.nbcio.modules.flowable.entity.SysCustomForm;
import com.nbcio.modules.flowable.entity.SysDeployForm;
import com.nbcio.modules.flowable.entity.SysForm;
import com.nbcio.modules.flowable.factory.FlowServiceFactory;
import com.nbcio.modules.flowable.flow.ExpressionCmd;
import com.nbcio.modules.flowable.service.IFlowDefinitionService;
import com.nbcio.modules.flowable.service.ISysCustomFormService;
import com.nbcio.modules.flowable.service.ISysDeployFormService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: ????????????
 * @Author: nbacheng
 * @Date:   2022-03-29
 * @Version: V1.0
 */
@Service
public class FlowDefinitionServiceImpl extends FlowServiceFactory implements IFlowDefinitionService {
    @Autowired
    IFlowThirdService iFlowThirdService;
    @Autowired
    FlowMyBusinessServiceImpl flowMyBusinessService;
    @Autowired
    FlowTaskServiceImpl flowTaskService;
    @Autowired
    ManagementService managementService;
    @Autowired
    ProcessEngineConfigurationImpl processEngineConfiguration;
    @Autowired
    private ISysDeployFormService sysDeployFormService;
    @Autowired
	private ISysCustomFormService sysCustomFormService;
    @Autowired
    FlowCommonService flowCommonService;


    private static final String BPMN_FILE_SUFFIX = ".bpmn";

    @Override
    public boolean exist(String processDefinitionKey) {
        ProcessDefinitionQuery processDefinitionQuery
                = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey);
        long count = processDefinitionQuery.count();
        return count > 0 ? true : false;
    }


    /**
     * ??????????????????
     *
     * @param pageNum  ????????????
     * @param pageSize ????????????
     * @param flowProcDefDto
     * @return ??????????????????????????????
     */
    @Override
    public Page<FlowProcDefDto> list(Integer pageNum, Integer pageSize, FlowProcDefDto flowProcDefDto) {
        Page<FlowProcDefDto> page = new Page<>();
        // ??????????????????????????????
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery
                //.processDefinitionId("cs:5:15e953ed-4d09-11ec-85b8-e884a5deddfc")
                .latestVersion()   //???????????????????????????
                .orderByProcessDefinitionKey().asc().orderByProcessDefinitionVersion().desc();
        /*=====??????=====*/
        if (StrUtil.isNotBlank(flowProcDefDto.getName())){
            processDefinitionQuery.processDefinitionNameLike("%"+flowProcDefDto.getName()+"%");
        }
        if (StrUtil.isNotBlank(flowProcDefDto.getCategory())){
            processDefinitionQuery.processDefinitionCategory(flowProcDefDto.getCategory());
        }
        if (flowProcDefDto.getSuspensionState() == 1){
            processDefinitionQuery.active();
        }
        /*============*/
        page.setTotal(processDefinitionQuery.count());
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage((pageNum - 1) * pageSize, pageSize);

        List<FlowProcDefDto> dataList = new ArrayList<>();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            FlowProcDefDto reProcDef = new FlowProcDefDto();
            BeanUtils.copyProperties(processDefinition, reProcDef);
            // ??????????????????
            reProcDef.setDeploymentTime(deployment.getDeploymentTime());
            SysForm sysForm = sysDeployFormService.selectSysDeployFormByDeployId(reProcDef.getDeploymentId());
            if (Objects.nonNull(sysForm)) {
            	reProcDef.setFormName(sysForm.getFormName());
            	reProcDef.setFormId(sysForm.getId());
            }
            
            SysCustomForm sysCustomForm = sysDeployFormService.selectSysCustomFormByDeployId(reProcDef.getDeploymentId());
            if (Objects.nonNull(sysCustomForm)) {
            	reProcDef.setFormName(sysCustomForm.getBusinessName());
            	reProcDef.setFormId(sysCustomForm.getId());
            }
            dataList.add(reProcDef);
        }
        page.setRecords(dataList);
        return page;
    }


    /**
     * ??????????????????
     *
     * @param name
     * @param category
     * @param in
     */
    @Override
    public void importFile(String name, String category, InputStream in) {
        Deployment deploy = repositoryService.createDeployment().addInputStream(name + BPMN_FILE_SUFFIX, in).name(name).category(category).deploy();
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        repositoryService.setProcessDefinitionCategory(definition.getId(), category);

    }

    /**
     * ??????xml
     *
     * @param deployId
     * @return
     */
    @Override
    public Result readXml(String deployId) throws IOException {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        return Result.OK("", result);
    }

    @Override
    public Result readXmlByDataId(String dataId) throws IOException {
        LambdaQueryWrapper<FlowMyBusiness> flowMyBusinessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flowMyBusinessLambdaQueryWrapper.eq(FlowMyBusiness::getDataId,dataId)
        ;
        //???????????????????????????????????????FlowCommonService.initActBusiness????????????????????????
        FlowMyBusiness business = flowMyBusinessService.getOne(flowMyBusinessLambdaQueryWrapper);
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(business.getProcessDefinitionId()).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        return Result.OK("", result);
    }

    @Override
    public Result readXmlByName(String processDefinitionName) throws IOException {
    	ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        String processId;
        if(processDefinitionQuery.processDefinitionName(processDefinitionName)
        		           .latestVersion().active().list().size() > 0) {
        	processId = processDefinitionQuery.processDefinitionName(processDefinitionName).processDefinitionCategory(Category.ddxz.name())
        			    .latestVersion().active().list().get(0).getId();
	    	ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processId).singleResult();
	        InputStream inputStream = repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getResourceName());
	        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
	        return Result.OK("", result);
        }
        else {
        	return Result.OK("", null);
        }
    }
    
    /**
     * ??????xml ????????????Id
     *
     * @param dataId
     * @return
     */
    @Override
    public InputStream readImageByDataId(String dataId) {
        FlowMyBusiness business = flowMyBusinessService.getByDataId(dataId);

        String processId = business.getProcessInstanceId();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        //??????????????? ????????????
        if (pi == null) {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(business.getProcessDefinitionId()).singleResult();
            return this.readImage(processDefinition.getDeploymentId());
        }

        List<HistoricActivityInstance> historyProcess = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processId).list();
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        //???????????????
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        for (HistoricActivityInstance hi : historyProcess) {
            String activityType = hi.getActivityType();
            if (activityType.equals("sequenceFlow") || activityType.equals("exclusiveGateway")) {
                flows.add(hi.getActivityId());
            } else if (activityType.equals("userTask") || activityType.equals("startEvent")) {
                activityIds.add(hi.getActivityId());
            }
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processId).list();
        for (Task task : tasks) {
            activityIds.add(task.getTaskDefinitionKey());
        }
        ProcessEngineConfiguration engConf = processEngine.getProcessEngineConfiguration();
        //???????????????????????????
        ProcessDiagramGenerator processDiagramGenerator = engConf.getProcessDiagramGenerator();
        InputStream in = processDiagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engConf.getActivityFontName(), engConf.getLabelFontName(), engConf.getAnnotationFontName(), engConf.getClassLoader(), 1.0, true);
        return in;
    }
    /**
     * ??????xml
     *
     * @param deployId
     * @return
     */
    @Override
    public InputStream readImage(String deployId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        //???????????????
        DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        //???????????????
        return diagramGenerator.generateDiagram(
                bpmnModel,
                "png",
                Collections.emptyList(),
                Collections.emptyList(),
                "??????",
                "??????",
                "??????",
                null,
                1.0,
                false);

    }

    /**
     * ??????????????????ID??????????????????
     *
     * @param procDefKey ????????????Id
     * @param variables ????????????
     * @return
     */
    @Override
    public Result startProcessInstanceByKey(String procDefKey, Map<String, Object> variables) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(procDefKey)
                .latestVersion().singleResult();
        return startProcessInstanceById(processDefinition.getId(),variables);
    }
    /**
     * ??????????????????ID???????????????????????????????????????dataid,???????????????dataid
     *
     * @param procDefId ????????????Id
     * @param variables ????????????
     * @return
     */
    @Override
    @Transactional
    public Result startProcessInstanceById(String procDefId, Map<String, Object> variables) {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(procDefId)
                    .latestVersion()
                    .singleResult();
            if (Objects.nonNull(processDefinition) && processDefinition.isSuspended()) {
                return Result.error("??????????????????,??????????????????");
            }
//           variables.put("skip", true);
//           variables.put(ProcessConstants.FLOWABLE_SKIP_EXPRESSION_ENABLED, true);
            // ?????????????????????Id????????????
            
            SysUser sysUser = iFlowThirdService.getLoginUser();
            identityService.setAuthenticatedUserId(sysUser.getUsername());
            variables.put(ProcessConstants.PROCESS_INITIATOR, sysUser.getUsername());
            //ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, variables);
            //?????????????????????dataid????????? add by nbacheng
            FlowMyBusiness flowmybusiness = flowMyBusinessService.getByDataId(variables.get("dataId").toString());
            String serviceImplName = flowmybusiness.getServiceImplName();
            FlowCallBackServiceI flowCallBackService = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplName);
            if (flowCallBackService!=null){
              Object businessDataById = flowCallBackService.getBusinessDataById(variables.get("dataId").toString());
              variables.put("formData",businessDataById);
            }
            //??????BusinessKey  modify nbacheng
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, variables.get("dataId").toString(), variables);
            // ?????????????????????????????????????????????????????????
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).active().singleResult();
            if (Objects.nonNull(task)) {
                taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.NORMAL.getType(), sysUser.getRealname() + "??????????????????");
                taskService.setAssignee(task.getId(), sysUser.getUsername());
                //taskService.complete(task.getId(), variables);
            }
            
        /*======================todo ????????????  ??????????????????????????????======================*/
        //????????????id
        String dataId = variables.get("dataId").toString();
        //???????????????????????????????????????FlowCommonService.initActBusiness????????????????????????
        FlowMyBusiness business = flowMyBusinessService.getByDataId(dataId);
        business.setProcessDefinitionId(procDefId)
                .setProcessInstanceId(processInstance.getProcessInstanceId())
                .setActStatus(ActStatus.start)
                .setProposer(sysUser.getUsername())
                .setTaskId(task.getId())
                .setTaskName(task.getName())
                .setTaskNameId(task.getId())
                .setPriority(String.valueOf(task.getPriority()))
                .setDoneUsers("")
                .setTodoUsers(JSON.toJSONString(sysUser.getRealname()));
        flowMyBusinessService.updateById(business);
        //spring????????????
        String serviceImplNameafter = business.getServiceImplName();
        FlowCallBackServiceI flowCallBackServiceafter = (FlowCallBackServiceI) SpringContextUtils.getBean(serviceImplNameafter);
        // ??????????????????????????????????????????
        business.setValues(variables);
        if (flowCallBackServiceafter!=null)flowCallBackServiceafter.afterFlowHandle(business);
        return Result.OK("??????????????????,???????????????????????????????????????????????????.");
    }
    
	/**
	 * ??????????????????ID????????????????????????????????????dataid?????????????????????????????????????????????????????????
	 *  add by nbacheng
	 * @param procDefId
	 *            ????????????Id
	 * @param variables
	 *            ????????????
	 * @return
	 */
	@Override
	@Transactional
	public Result startProcessInstanceByProcDefId(String procDefId, Map<String, Object> variables) {
		try {
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(procDefId).latestVersion().singleResult();
			if (Objects.nonNull(processDefinition) && processDefinition.isSuspended()) {
				return Result.error("??????????????????,??????????????????");
			}
			// variables.put("skip", true);
			// variables.put(ProcessConstants.FLOWABLE_SKIP_EXPRESSION_ENABLED,
			// true);
			// ?????????????????????Id????????????

			SysUser sysUser = iFlowThirdService.getLoginUser();
			identityService.setAuthenticatedUserId(sysUser.getUsername());
			variables.put(ProcessConstants.PROCESS_INITIATOR, sysUser.getUsername());
			ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, variables);
			// ?????????????????????????????????????????????????????????
			Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).active()
					.singleResult();
			if (Objects.nonNull(task)) {
				taskService.addComment(task.getId(), processInstance.getProcessInstanceId(),
						FlowComment.NORMAL.getType(), sysUser.getRealname() + "??????????????????");
				taskService.setAssignee(task.getId(), sysUser.getUsername());
				//taskService.complete(task.getId(), variables);
			}

			// ????????????
			//FlowNextDto nextFlowNode = flowTaskService.getNextFlowNode(task.getId(), variables);
			//taskService.complete(task.getId(), variables);
			return Result.OK("??????????????????,???????????????????????????????????????????????????.");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("??????????????????");
		}

	}
    
	/**
	 * ????????????dataId,serviceName?????????????????????????????????????????????????????????????????????
	 *  add by nbacheng
	 * @param dataId,serviceName
	 *           
	 * @param variables
	 *            ????????????
	 * @return
	 */
    @Override
    public Result startProcessInstanceByDataId(String dataId, String serviceName, Map<String, Object> variables) {
    	//??????????????????????????????????????????????????????
    	
        if (serviceName==null){
             return Result.error("?????????serviceName???"+serviceName);
        }
        SysCustomForm sysCustomForm = sysCustomFormService.selectSysCustomFormByServiceName(serviceName);
        if(sysCustomForm ==null){
        	 return Result.error("?????????sysCustomForm???"+serviceName);
        }
        //???????????????????????????????????????????????????????????????????????????????????????
        ProcessDefinition processDefinition;
        String deployId = sysCustomForm.getDeployId();
        if(StringUtils.isEmpty(deployId)) {
        	SysDeployForm sysDeployForm  = sysDeployFormService.selectSysDeployFormByFormId(sysCustomForm.getId());
            if(sysDeployForm ==null){          	
       	       return Result.error("??????????????????????????????????????????,????????????????????????????????????"+sysCustomForm.getId());
            }
            processDefinition = repositoryService.createProcessDefinitionQuery()
        		.parentDeploymentId(sysDeployForm.getDeployId()).latestVersion().singleResult();
        }
        else {
        	processDefinition = repositoryService.createProcessDefinitionQuery()
            		.parentDeploymentId(deployId).latestVersion().singleResult();
        }
        
        LambdaQueryWrapper<FlowMyBusiness> flowMyBusinessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flowMyBusinessLambdaQueryWrapper.eq(FlowMyBusiness::getDataId, dataId);
        FlowMyBusiness business = flowMyBusinessService.getOne(flowMyBusinessLambdaQueryWrapper);
        if (business==null){
        	boolean binit = flowCommonService.initActBusiness(sysCustomForm.getBusinessName(), dataId, serviceName, 
        	processDefinition.getKey(), processDefinition.getId(), sysCustomForm.getRouteName());
        	if(!binit) {
        		return Result.error("??????????????????????????????????????????,????????????????????????????????????"+sysCustomForm.getId());
        	}
            FlowMyBusiness businessnew = flowMyBusinessService.getOne(flowMyBusinessLambdaQueryWrapper);
           //?????????????????????????????????
            if (StrUtil.isNotBlank(businessnew.getProcessDefinitionId())){
              return this.startProcessInstanceById(businessnew.getProcessDefinitionId(),variables);
            }
            return this.startProcessInstanceByKey(businessnew.getProcessDefinitionKey(),variables);
        }
        else {
        	 return Result.error("??????????????????dataid??????????????????????????????"+dataId);
        }
        
    }


    /**
     * ???????????????????????????
     *
     * @param state    ?????? ??????1 ??????2
     * @param deployId ????????????ID
     */
    @Override
    public void updateState(Integer state, String deployId) {
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        // ??????
        if (state == 1) {
            repositoryService.activateProcessDefinitionById(procDef.getId(), true, null);
        }
        // ??????
        if (state == 2) {
            repositoryService.suspendProcessDefinitionById(procDef.getId(), true, null);
        }
    }


    /**
     * ??????????????????
     *
     * @param deployId ????????????ID act_ge_bytearray ?????? deployment_id???
     */
    @Override
    public void delete(String deployId) {
        // true ?????????????????? ,?????????????????????????????????????????????
        repositoryService.deleteDeployment(deployId, true);
    }


    /**
     *???????????????????????????????????????  add by nbacheng
     *
     * @param processDefinitionName ??????ID act_re_procdef ?????? name???
     */
	@Override
	public JSONArray ListAllNode(String processDefinitionName) {
		// TODO Auto-generated method stub

		// ??????????????????????????????
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        String processId;
        JSONArray jsonlist = new JSONArray();
        List<Object> taskList = new ArrayList<>();
        if(processDefinitionQuery.processDefinitionName(processDefinitionName).processDefinitionCategory(Category.ddxz.name())
        		           .latestVersion().active().list().size() > 0) {
        	processId = processDefinitionQuery.processDefinitionName(processDefinitionName)
        		           .latestVersion().active().list().get(0).getId();
        	List<org.flowable.bpmn.model.Process> processes = repositoryService.getBpmnModel(processId). getProcesses();
			System.out.println("processes size:" + processes.size());
			
			for (org.flowable.bpmn.model.Process process : processes) {
			    Collection<FlowElement> flowElements = process.getFlowElements();
			    getTaskList(processId, taskList, flowElements, null, null);
			    /*if (CollectionUtils.isNotEmpty(flowElements)) {
			    	int i=1;
			        for (FlowElement flowElement : flowElements) {
			            if (flowElement instanceof UserTask) {  //????????????
			            	JSONObject jsonobj = new JSONObject();  
			            	UserTask userTask = (UserTask) flowElement;
			            	jsonobj.put("NodeNo", i);
			            	jsonobj.put("NodeName", userTask.getName());
			            	jsonobj.put("Assignee", userTask.getAssignee());
			            	jsonlist.add(jsonobj);
			            	i++;
			                System.out.println("UserTask???" + userTask.getName());
			                System.out.println("getAssignee???" + userTask.getAssignee());
			                
			                //if(userTask.getCandidateGroups().size()>0) {
			                //	System.out.println("getCandidateGroups???" + userTask.getCandidateGroups().get(0));
			                //}
			               //????????????
			            }
			            if (flowElement instanceof SubProcess) {//?????????
			               //?????????
			            }
			 
			         }
			     }*/
			}
         }
        int i=1;
        for (Object flowElement : taskList) {
        	JSONObject jsonobj = new JSONObject();  
        	UserTask userTask = (UserTask) flowElement;
        	SysUser sysUser  = iFlowThirdService.getUserByUsername(userTask.getAssignee());
        	jsonobj.put("NodeNo", i);
        	jsonobj.put("NodeName", userTask.getName());
        	jsonobj.put("Assignee", userTask.getAssignee());
        	jsonobj.put("RealName", sysUser.getRealname());
        	jsonlist.add(jsonobj);
        	i++;
        	System.out.println("UserTask???" + userTask.getName());
            System.out.println("getAssignee???" + userTask.getAssignee());
        }
     
        return jsonlist;
		 /*System.out.println("processes size:" + processes.size());
		List<List<NextNode>> nextNodes = new ArrayList<>();
		for (Process process : processes) {
		    Collection<FlowElement> flowElements = process.getFlowElements();
		    if (CollectionUtils.isNotEmpty(flowElements)) {
		        for (FlowElement flowElement : flowElements) {
		            if (flowElement instanceof UserTask) {
		                System.out.println("UserTask???" + flowElement.getName());
		               //????????????
		            }
		            if (flowElement instanceof SubProcess) {
		               //?????????
		            }
		 
		        }
		    }
		}*/
	}
	/***
     * ??????bpmnmodel?????????????????????????????????
     * @param processInstanceId
     * @param taskList
     * @param flowElements
     * @param workflowRequestFormData
     * @param curFlowElement
     */
    private void getTaskList(String processInstanceId, List<Object> taskList, Collection<FlowElement> flowElements, Map<String, Object> workflowRequestFormData, FlowElement curFlowElement) {
        if (curFlowElement == null && taskList.size() == 0) {
            // ???????????????UserTask
            FlowElement startElement = flowElements.stream().filter(flowElement -> flowElement instanceof StartEvent).collect(Collectors.toList()).get(0);
            List<SequenceFlow> outgoingFlows = ((StartEvent) startElement).getOutgoingFlows();
            String targetRef = outgoingFlows.get(0).getTargetRef();
            // ??????ID??????FlowElement
            FlowElement targetElementOfStartElement = getFlowElement(flowElements, targetRef);
            if (targetElementOfStartElement instanceof UserTask) {
                this.getTaskList(processInstanceId, taskList, flowElements, workflowRequestFormData, targetElementOfStartElement);
            }

        } else if (curFlowElement instanceof UserTask) {
            // ??????Usertask?????????????????????
            taskList.add(curFlowElement);
            String targetRef = "";
            List<SequenceFlow> outgoingFlows = ((UserTask) curFlowElement).getOutgoingFlows();
            if (outgoingFlows.size() == 1) {
                targetRef = outgoingFlows.get(0).getTargetRef();
            } else {
                // ????????????????????????sequenceFlow???
                SequenceFlow sequenceFlow = getSequenceFlow(workflowRequestFormData, outgoingFlows);
                if (sequenceFlow != null) {
                    targetRef = sequenceFlow.getTargetRef();
                }
            }
            // ??????ID??????FlowElement
            FlowElement targetElement = getFlowElement(flowElements, targetRef);

            this.getTaskList(processInstanceId, taskList, flowElements, workflowRequestFormData, targetElement);
        } else if (curFlowElement instanceof ExclusiveGateway) {
            String targetRef = "";
            // ?????????????????????????????????????????????sequenceFlow?????????FlowElement
            List<SequenceFlow> exclusiveGatewayOutgoingFlows = ((ExclusiveGateway) curFlowElement).getOutgoingFlows();
            // ????????????????????????sequenceFlow???
            SequenceFlow sequenceFlow = getSequenceFlow(workflowRequestFormData, exclusiveGatewayOutgoingFlows);
            if (sequenceFlow != null) {
                targetRef = sequenceFlow.getTargetRef();
            }
            // ??????ID??????FlowElement
            FlowElement targetElement = getFlowElement(flowElements, targetRef);

            this.getTaskList(processInstanceId, taskList, flowElements, workflowRequestFormData, targetElement);
        }
    }

/***
     * ??????ID??????FlowElement
     * @param flowElements
     * @param targetRef
     * @return
     */
    private FlowElement getFlowElement(Collection<FlowElement> flowElements, String targetRef) {
        List<FlowElement> targetFlowElements = flowElements.stream().filter(
                flowElement -> !StringUtils.isEmpty(flowElement.getId()) && flowElement.getId().equals(targetRef)
        ).collect(Collectors.toList());
        if (targetFlowElements.size() > 0) {
            return targetFlowElements.get(0);
        }
        return null;
    }

  /***
     * ????????????????????????sequenceFlow???
     * @param workflowRequestFormData
     * @param outgoingFlows
     * @return
     */
    private SequenceFlow getSequenceFlow(Map workflowRequestFormData, List<SequenceFlow> outgoingFlows) {

        List<SequenceFlow> sequenceFlows = outgoingFlows.stream().filter(item -> {
            Object execConditionExpressionResult = null;
            boolean re = false;
            try {
                execConditionExpressionResult = this.getElValue(item.getConditionExpression(), workflowRequestFormData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (execConditionExpressionResult.equals("true")) {
                re = true;
            }
            return (Boolean) execConditionExpressionResult;
        }).collect(Collectors.toList());
        if (sequenceFlows.size() > 0) {
            return sequenceFlows.get(0);

        }
        return null;
    }
    private boolean getElValue(String exp, Map<String, Object> variableMap){
        return managementService.executeCommand(new ExpressionCmd(runtimeService, processEngineConfiguration, null, exp, variableMap));
    }

}
