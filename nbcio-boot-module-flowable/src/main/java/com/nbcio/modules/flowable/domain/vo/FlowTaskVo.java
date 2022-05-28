package com.nbcio.modules.flowable.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.nbcio.modules.flowable.domain.dto.FlowCommentFileDto;

/**
 * <p>流程任务<p>
 *
 */
@Data
@ApiModel("工作流任务相关--请求参数")
public class FlowTaskVo {

    @ApiModelProperty("数据Id")
    private String dataId;
    @ApiModelProperty("任务Id")
    private String taskId;

    @ApiModelProperty("用户Id")
    private String userId;

    @ApiModelProperty("任务意见")
    private String comment;
    
    @ApiModelProperty("任务附件")
    private FlowCommentFileDto commentFileDto;

    @ApiModelProperty("流程实例Id")
    private String instanceId;

    @ApiModelProperty("节点")
    private String targetKey;

    @ApiModelProperty("流程变量信息")
    private Map<String, Object> values;

    @ApiModelProperty("审批人")
    private String assignee;

    @ApiModelProperty("候选人")
    private List<String> candidateUsers;

    @ApiModelProperty("审批组")
    private List<String> candidateGroups;
}
