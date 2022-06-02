package com.nbcio.modules.estar.entity;

import java.io.Serializable;

import org.jeecgframework.poi.excel.annotation.Excel;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: oa_subdep
 * @Author: nbacheng
 * @Date:   2022-02-24
 * @Version: V1.0
 */
@Data
@TableName("oa_subdep")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="oa_subdep对象", description="oa_subdep")
public class OaSubdep implements Serializable {
    private static final long serialVersionUID = 1L;

	/**depno*/
	@Excel(name = "depno", width = 15)
    @ApiModelProperty(value = "depno")
    private java.lang.String depno;
	/**depname*/
	@Excel(name = "depname", width = 15)
    @ApiModelProperty(value = "depname")
    private java.lang.String depname;
}
