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
import org.jeecg.common.aspect.annotation.Dict;

import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: oa_zbmhzb
 * @Author: nbacheng
 * @Date:   2022-02-24
 * @Version: V1.0
 */
@Data
@TableName("oa_zbmhzb")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="oa_zbmhzb对象", description="oa_zbmhzb")
public class OaZbmhzb implements Serializable {
    private static final long serialVersionUID = 1L;

    /**depname*/
	@Excel(name = "depname", width = 15)
    @ApiModelProperty(value = "depname")
    private java.lang.String depname;
	/**rs*/
	@Excel(name = "rs", width = 15)
    @ApiModelProperty(value = "rs")
    private java.lang.Integer rs;
	/**bysfgz*/
	@Excel(name = "bysfgz", width = 15)
    @ApiModelProperty(value = "bysfgz")
    private java.math.BigDecimal bysfgz;
	/**byjbf*/
	@Excel(name = "byjbf", width = 15)
    @ApiModelProperty(value = "byjbf")
    private java.math.BigDecimal byjbf;
	/**bysfjj*/
	@Excel(name = "bysfjj", width = 15)
    @ApiModelProperty(value = "bysfjj")
    private java.math.BigDecimal bysfjj;
	/**sysfgz*/
	@Excel(name = "sysfgz", width = 15)
    @ApiModelProperty(value = "sysfgz")
    private java.math.BigDecimal sysfgz;
	/**syjbf*/
	@Excel(name = "syjbf", width = 15)
    @ApiModelProperty(value = "syjbf")
    private java.math.BigDecimal syjbf;
	/**sysfjj*/
	@Excel(name = "sysfjj", width = 15)
    @ApiModelProperty(value = "sysfjj")
    private java.math.BigDecimal sysfjj;
}
