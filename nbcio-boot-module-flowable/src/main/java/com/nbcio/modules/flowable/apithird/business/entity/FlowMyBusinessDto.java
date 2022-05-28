package com.nbcio.modules.flowable.apithird.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Map;

/**
 * @Description: 流程业务扩展表
 * @Author: nbacheng
 * @Date:   2021-11-25
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="flow_my_business对象", description="流程业务扩展表")
public class FlowMyBusinessDto implements Serializable {
    private static final long serialVersionUID = 1L;

	/**流程定义key 一个key会有多个版本的id*/
	@TableField(exist = false)
	@Excel(name = "流程定义key 一个key会有多个版本的id", width = 15)
    @ApiModelProperty(value = "流程定义key 一个key会有多个版本的id")
    private String processDefinitionKey;
	/**流程定义id 一个流程定义唯一*/
    @TableField(exist = false)
	@Excel(name = "流程定义id 一个流程定义唯一", width = 15)
    @ApiModelProperty(value = "流程定义id 一个流程定义唯一")
    private String processDefinitionId;
	/**流程业务实例id 一个流程业务唯一，本表中也唯一*/
    @TableField(exist = false)
	@Excel(name = "流程业务实例id 一个流程业务唯一，本表中也唯一", width = 15)
    @ApiModelProperty(value = "流程业务实例id 一个流程业务唯一，本表中也唯一")
    private String processInstanceId;
	/**流程业务简要描述*/
    @TableField(exist = false)
	@Excel(name = "流程业务简要描述", width = 15)
    @ApiModelProperty(value = "流程业务简要描述")
    private String title;
	/**业务表id，理论唯一*/
    @TableField(exist = false)
	@Excel(name = "业务表id，理论唯一", width = 15)
    @ApiModelProperty(value = "业务表id，理论唯一")
    private String dataId;
	/**业务类名，用来获取spring容器里的服务对象*/
    @TableField(exist = false)
	@Excel(name = "业务类名，用来获取spring容器里的服务对象", width = 15)
    @ApiModelProperty(value = "业务类名，用来获取spring容器里的服务对象")
    private String serviceImplName;
	/**申请人*/
    @TableField(exist = false)
	@Excel(name = "申请人", width = 15)
    @ApiModelProperty(value = "申请人")
    private String proposer;
	/**流程状态说明，有：启动  撤回  驳回  审批中  审批通过  审批异常*/
    @TableField(exist = false)
	@Excel(name = "流程状态说明，有：启动  撤回  驳回  审批中  审批通过  审批异常", width = 15)
    @ApiModelProperty(value = "流程状态说明，有：启动  撤回  驳回  审批中  审批通过  审批异常")
    private String actStatus;
	/**当前的节点实例上的Id*/
    @TableField(exist = false)
	@Excel(name = "当前的节点Id", width = 15)
    @ApiModelProperty(value = "当前的节点Id")
    private String taskId;
	/**当前的节点*/
    @TableField(exist = false)
	@Excel(name = "当前的节点", width = 15)
    @ApiModelProperty(value = "当前的节点")
    private String taskName;
	/**当前的节点定义上的Id*/
    @TableField(exist = false)
	@Excel(name = "当前的节点", width = 15)
    @ApiModelProperty(value = "当前的节点")
    private String taskNameId;
	/**当前的节点可以处理的用户名，为username的集合json字符串*/
    @TableField(exist = false)
	@Excel(name = "当前的节点可以处理的用户名", width = 15)
    @ApiModelProperty(value = "当前的节点可以处理的用户名")
    private String todoUsers;
	/**处理过的人,为username的集合json字符串*/
    @TableField(exist = false)
	@Excel(name = "处理过的人", width = 15)
    @ApiModelProperty(value = "处理过的人")
    private String doneUsers;
	/**当前任务节点的优先级 流程定义的时候所填*/
    @TableField(exist = false)
	@Excel(name = "当前任务节点的优先级 流程定义的时候所填", width = 15)
    @ApiModelProperty(value = "当前任务节点的优先级 流程定义的时候所填")
    private String priority;
	/**流程变量*/
	@TableField(exist = false)
    private Map<String,Object> values;
	/**前端页面显示的路由地址，理论唯一*/
	@TableField(exist = false)
	@Excel(name = "路由地址，理论唯一", width = 15)
    @ApiModelProperty(value = "路由地址，理论唯一")
    private String routeName;
}
