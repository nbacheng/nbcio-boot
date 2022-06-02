package com.nbcio.modules.estar.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: OA工资表
 * @Author: nbacheng
 * @Date:   2022-05-12
 * @Version: V1.0
 */
@Data
@TableName("oa_salary")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="oa_salary对象", description="OA工资表")
public class OaSalary implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**人员*/
	@Excel(name = "人员", width = 15)
    @ApiModelProperty(value = "人员")
    private java.lang.String username;
	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private java.lang.String realname;
	/**部门编号*/
	@Excel(name = "部门编号", width = 15)
    @ApiModelProperty(value = "部门编号")
    private java.lang.String orgCode;
	/**部门名称*/
	@Excel(name = "部门名称", width = 15)
    @ApiModelProperty(value = "部门名称")
    private java.lang.String depname;
	/**年*/
	@Excel(name = "年", width = 15)
    @ApiModelProperty(value = "年")
    private java.lang.Integer salaryyear;
	/**月*/
	@Excel(name = "月", width = 15)
    @ApiModelProperty(value = "月")
    private java.lang.Integer salarymonth;
	/**人数*/
	@Excel(name = "人数", width = 15)
    @ApiModelProperty(value = "人数")
    private java.lang.Integer rs;
	/**本月实发工资*/
	@Excel(name = "本月实发工资", width = 15)
    @ApiModelProperty(value = "本月实发工资")
    private java.lang.Double bysfgz;
	/**本月加班费*/
	@Excel(name = "本月加班费", width = 15)
    @ApiModelProperty(value = "本月加班费")
    private java.lang.Double byjbf;
	/**本月实发奖金*/
	@Excel(name = "本月实发奖金", width = 15)
    @ApiModelProperty(value = "本月实发奖金")
    private java.lang.Double bysfjj;
	/**上月实发工资*/
	@Excel(name = "上月实发工资", width = 15)
    @ApiModelProperty(value = "上月实发工资")
    private java.lang.Double sysfgz;
	/**上月加班费*/
	@Excel(name = "上月加班费", width = 15)
    @ApiModelProperty(value = "上月加班费")
    private java.lang.Double syjbf;
	/**上月实发奖金*/
	@Excel(name = "上月实发奖金", width = 15)
    @ApiModelProperty(value = "上月实发奖金")
    private java.lang.String sysfjj;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
}
