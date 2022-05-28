package com.nbcio.modules.flowable.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;

import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 系统自定义表单表
 * @Author: nbacheng
 * @Date:   2022-04-23
 * @Version: V1.0
 */
@Data
@TableName("sys_custom_form")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_custom_form对象", description="系统自定义表单表")
public class SysCustomForm implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**业务表单名称*/
	@Excel(name = "业务表单名称", width = 15)
    @ApiModelProperty(value = "业务表单名称")
    private java.lang.String businessName;
	/**业务服务名称*/
	@Excel(name = "业务服务名称", width = 15)
    @ApiModelProperty(value = "业务服务名称")
    private java.lang.String businessService;
	/**关联流程发布主键*/
	@Excel(name = "关联流程发布主键", width = 15)
    @ApiModelProperty(value = "关联流程发布主键")
    private java.lang.String deployId;
	/**前端路由地址*/
	@Excel(name = "前端路由地址", width = 15)
    @ApiModelProperty(value = "前端路由地址")
    private java.lang.String routeName;
	/**组件注入方法*/
	@Excel(name = "组件注入方法", width = 15)
    @ApiModelProperty(value = "组件注入方法")
    private java.lang.String component;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
}
