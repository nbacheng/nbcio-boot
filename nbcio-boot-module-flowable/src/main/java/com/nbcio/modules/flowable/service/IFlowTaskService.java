package com.nbcio.modules.flowable.service;

import org.flowable.task.api.Task;

import org.jeecg.common.api.vo.Result;
import com.nbcio.modules.flowable.domain.dto.FlowTaskDto;
import com.nbcio.modules.flowable.domain.vo.FlowTaskVo;

import java.io.InputStream;
import java.util.Map;

/**
 */
public interface IFlowTaskService {

    /**
     * 审批任务
     *
     * @param task 请求实体参数
     */
    Result complete(FlowTaskVo task);
    
    /**
     * 审批任务
     *
     * @param task 请求实体参数 ，有业务dataid
     */
	//Result completeForDataID(FlowTaskVo taskVo); 

    /**
     * 驳回任务
     *
     * @param flowTaskVo
     */
    void taskReject(FlowTaskVo flowTaskVo);
    /**
     * 驳回任务
     *
     * @param flowTaskVo 请求业务DataId
     */
	void taskRejectForDataId(FlowTaskVo flowTaskVo);

    /**
     * 退回任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void taskReturn(FlowTaskVo flowTaskVo);
    /**
     * 退回任务
     *
     * @param flowTaskVo 请求实体参数 请求业务DataId
     */
	void taskReturnForDataId(FlowTaskVo flowTaskVo);

    /**
     * 获取所有可回退的节点
     *
     * @param flowTaskVo
     * @return
     */
    Result findReturnTaskList(FlowTaskVo flowTaskVo);


    /**
     * 删除任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void deleteTask(FlowTaskVo flowTaskVo);

    /**
     * 认领/签收任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void claim(FlowTaskVo flowTaskVo);

    /**
     * 取消认领/签收任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void unClaim(FlowTaskVo flowTaskVo);

    /**
     * 委派任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void delegateTask(FlowTaskVo flowTaskVo);


    /**
     * 转办任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void assignTask(FlowTaskVo flowTaskVo);

    /**
     * 所有流程任务
     * @param pageNum
     * @param pageSize
     * @param flowTaskDto
     * @return
     */
	Result allProcess(Integer pageNo, Integer pageSize, FlowTaskDto flowTaskDto);
    
    /**
     * 我发起的流程
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result myProcess(Integer pageNum, Integer pageSize);

    /**
     * 我发起的流程
     * @param pageNo
     * @param pageSize
     * @return
     */
	Result myProcessNew(Integer pageNo, Integer pageSize, FlowTaskDto flowTaskDto);
	
    /**
     * 取消申请
     * @param flowTaskVo
     * @return
     */
    Result stopProcess(FlowTaskVo flowTaskVo);

    /**
     * 撤回流程
     * @param flowTaskVo
     * @return
     */
    Result revokeProcess(FlowTaskVo flowTaskVo);
    
    Result revokeProcessForDataId(FlowTaskVo flowTaskVo);


    /**
     * 代办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return
     */
    Result todoList(Integer pageNum, Integer pageSize);

	Result todoListNew(Integer pageNo, Integer pageSize, FlowTaskDto flowTaskDto);

    /**
     * 已办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return
     */
    Result finishedList(Integer pageNum, Integer pageSize);

	Result finishedListNew(Integer pageNum, Integer pageSize, FlowTaskDto flowTaskDto);
    /**
     * 流程历史流转记录
     *
     * @param procInsId 流程实例Id, 流程发布id
     * @return
     */
    Result flowRecord(String procInsId,String deployId, String businessKey);

    /**
     * 流程历史流转记录
     *
     * @param 业务dataId
     * @return
     */
	Result flowRecordBydataid(String dataId);
    
    /**
     * 根据任务ID查询挂载的表单信息
     *
     * @param taskId 任务Id
     * @return
     */
    Task getTaskForm(String taskId);

    /**
     * 获取流程过程图
     * @param processId
     * @return
     */
    InputStream diagram(String processId);

    /**
     * 获取流程执行过程
     * @param procInsId
     * @return
     */
    Result getFlowViewer(String procInsId);
    Result getFlowViewerByDataId(String dataId);
    Result getFlowViewerByName(String processDefinitionName); //add by nbacheng
    
    /**
     * 获取流程变量
     * @param taskId
     * @return
     */
    Result processVariables(String taskId);

    /**
     * 获取下一节点
     * @param flowTaskVo 任务
     * @return
     */
    Result getNextFlowNode(FlowTaskVo flowTaskVo);

    /**
     * 流程历史当前记录信息
     * add by nbacheng
     * @param  procInsId 流程实例Id
     * @return
     */
    Map<String, Object> currentFlowRecord(String procInsId);


}
