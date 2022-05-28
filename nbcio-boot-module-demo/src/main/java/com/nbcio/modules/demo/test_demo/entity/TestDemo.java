package com.nbcio.modules.demo.test_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import com.nbcio.modules.flowable.apithird.business.entity.FlowMyBusinessDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 测试用户表
 * @Author: nbacheng
 * @Date:   2021-11-30
 * @Version: V1.0
 */
@Data
@TableName("test_demo")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="test_demo对象", description="测试用户表")
public class TestDemo extends FlowMyBusinessDto implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人登录名称*/
    @ApiModelProperty(value = "创建人登录名称")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人登录名称*/
    @ApiModelProperty(value = "更新人登录名称")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private String name;
	/**性别*/
	@Excel(name = "性别", width = 15, dicCode = "sex")
	@Dict(dicCode = "sex")
    @ApiModelProperty(value = "性别")
    private String sex;
	/**年龄*/
	@Excel(name = "年龄", width = 15)
    @ApiModelProperty(value = "年龄")
    private Integer age;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String descc;
	/**生日*/
	@Excel(name = "生日", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "生日")
    private Date birthday;
	/**用户编码*/
	@Excel(name = "用户编码", width = 15)
    @ApiModelProperty(value = "用户编码")
    private String userCode;
	/**头像*/
	@Excel(name = "头像", width = 15)
    @ApiModelProperty(value = "头像")
    private String topPic;
	/**附件*/
	@Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private String fileKk;
	/**城市*/
	@Excel(name = "城市", width = 15)
    @ApiModelProperty(value = "城市")
    private String chegnshi;
	/**弹窗*/
	@Excel(name = "弹窗", width = 15)
    @ApiModelProperty(value = "弹窗")
    private String pop;
	/**checkbox*/
	@Excel(name = "checkbox", width = 15, dicCode = "sex")
	@Dict(dicCode = "sex")
    @ApiModelProperty(value = "checkbox")
    private String ceck;
	/**下拉多选*/
	@Excel(name = "下拉多选", width = 15, dicCode = "sex")
	@Dict(dicCode = "sex")
    @ApiModelProperty(value = "下拉多选")
    private String xiamuti;
	/**搜索下拉*/
	@Excel(name = "搜索下拉", width = 15, dictTable = "sys_role", dicText = "role_name", dicCode = "role_code")
	@Dict(dictTable = "sys_role", dicText = "role_name", dicCode = "role_code")
    @ApiModelProperty(value = "搜索下拉")
    private String searchSel;
	/**下拉字典表*/
	@Excel(name = "下拉字典表", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "下拉字典表")
    private String selTable;
}
