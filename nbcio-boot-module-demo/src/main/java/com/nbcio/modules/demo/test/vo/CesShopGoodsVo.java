package com.nbcio.modules.demo.test.vo;

import java.io.Serializable;

import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Description: 商品视图增加两项num与zongprice
 * @Author: nbacheng
 * @Date:   2022-05-15
 * @Version: V1.0
 */
@Data

public class CesShopGoodsVo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
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
	/**商品名字*/
	@Excel(name = "商品名字", width = 15)
    @ApiModelProperty(value = "商品名字")
    private java.lang.String name;
	/**价格*/
	@Excel(name = "价格", width = 15)
    @ApiModelProperty(value = "价格")
    private java.math.BigDecimal price;
	/**出厂时间*/
	@Excel(name = "出厂时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "出厂时间")
    private java.util.Date chucDate;
	/**商品简介*/
	@Excel(name = "商品简介", width = 15)
    @ApiModelProperty(value = "商品简介")
    private java.lang.String contents;
	/**商品分类*/
	@Excel(name = "商品分类", width = 15, dictTable = "ces_shop_type", dicText = "name", dicCode = "id")
	@Dict(dictTable = "ces_shop_type", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "商品分类")
    private java.lang.String goodTypeId;
	/**编码跟上面id一样，主要为了前端避开id使用，否则JEditableTable会有问题，因为用到id关键字*/
	@ApiModelProperty(value = "编码")
	private java.lang.String code;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigInteger num;
	/**总价*/
	@Excel(name = "总价", width = 15)
    @ApiModelProperty(value = "总价")
    private java.math.BigDecimal zongprice;
}
