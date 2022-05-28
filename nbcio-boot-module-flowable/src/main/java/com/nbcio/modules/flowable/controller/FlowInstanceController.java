package com.nbcio.modules.flowable.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.jeecg.common.api.vo.Result;
import com.nbcio.modules.flowable.service.IFlowInstanceService;

/**
 * <p>工作流流程实例管理<p>
 */
@Slf4j
@Api(tags = "工作流流程实例管理")
@RestController
@RequestMapping("/flowable/instance")
public class FlowInstanceController {

    @Autowired
    private IFlowInstanceService flowInstanceService;

    /*@ApiOperation(value = "根据流程定义id启动流程实例")
    @PostMapping("/startBy/{procDefId}")
    public Result startById(@ApiParam(value = "流程定义id") @PathVariable(value = "procDefId") String procDefId,
                                @ApiParam(value = "变量集合,json对象") @RequestBody Map<String, Object> variables) {
        return flowInstanceService.startProcessInstanceById(procDefId, variables);

    }*/

    @ApiOperation(value = "列出流程实例")
    @DeleteMapping(value = "/queryListByInstanceId")
    public Result queryListByInstanceId(@ApiParam(value = "流程实例ID", required = true) @RequestParam String instanceId) {
    	List<Task> listtask =  flowInstanceService.queryListByInstanceId(instanceId);
    	/*for(Task task : listtask) {
    		System.out.println("task：" + task.getName());
    		System.out.println("getAssignee：" + task.getAssignee());
    	}*/
        return Result.OK(listtask);
    }
    
    
    @ApiOperation(value = "激活或挂起流程实例")
    @PostMapping(value = "/updateState")
    public Result updateState(@ApiParam(value = "1:激活,2:挂起", required = true) @RequestParam Integer state,
                              @ApiParam(value = "流程实例ID", required = true) @RequestParam String instanceId) {
        flowInstanceService.updateState(state,instanceId);
        return Result.OK("激活或挂起流程实例成功");
    }

    /*@ApiOperation("结束流程实例")
    @PostMapping(value = "/stopProcessInstance")
    public Result stopProcessInstance(@RequestBody FlowTaskVo flowTaskVo) {
        flowInstanceService.stopProcessInstance(flowTaskVo);
        return Result.OK();
    }*/

    @ApiOperation(value = "删除流程实例")
    @DeleteMapping(value = "/delete")
    public Result delete(@ApiParam(value = "流程实例ID", required = true) @RequestParam String instanceId,
                             @ApiParam(value = "删除原因") @RequestParam(required = false) String deleteReason,
                             @ApiParam(value = "业务dataId") @RequestParam(required = false) String dataId) {
        flowInstanceService.delete(instanceId,deleteReason,dataId);
        return Result.OK("删除流程实例成功");
    }

}
