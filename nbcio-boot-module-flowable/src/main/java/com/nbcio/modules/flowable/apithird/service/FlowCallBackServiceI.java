package com.nbcio.modules.flowable.apithird.service;

import java.util.List;
import java.util.Map;

import com.nbcio.modules.flowable.apithird.business.entity.FlowMyBusiness;

/**
 * 业务层实现接口方法，用于流程处理后的回调
 * @author PanMeiCheng
 * @version 1.0
 * @date 2021/11/26
 */
public interface FlowCallBackServiceI {
    /**
     * 流程处理完成后的回调
     * @param business 里面包含流程运行的现状信息，业务层可根据其信息判断，书写增强业务逻辑的代码，<br/>
     *                 1、比如将其中关键信息存入业务表，即可单表业务操作,否则需要关联flow_my_business表获取流程信息<br/>
     *                 2、比如在流程进行到某个节点时(business.taskId)，需要特别进行某些业务逻辑操作等等<br/>
     */
    void afterFlowHandle(FlowMyBusiness business);


    /**
     * 根据业务id返回业务表单数据<br/>
     * @param dataId
     * @return
     */
    Object getBusinessDataById(String dataId);

    /**
     * 返回当前节点的流程变量
     * @param taskNameId 节点定义id
     * @param values 前端传入的变量，里面包含dataId
     * @return
     */
    Map<String, Object> flowValuesOfTask(String taskNameId, Map<String, Object> values);

    /**
     * 返回当前节点的候选人username
     * @param taskNameId 节点定义id
     * @param values 前端传入的变量，里面包含dataId
     * @return
     */
    List<String> flowCandidateUsernamesOfTask(String taskNameId, Map<String, Object> values);
}
