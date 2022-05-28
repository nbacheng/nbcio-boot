package com.nbcio.modules.demo.test.entity;

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
 * @Description: 商品
 * @Author: nbacheng
 * @Date:   2022-05-15
 * @Version: V1.0
 */
@Data
@TableName("ces_shop_goods")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ces_shop_goods对象", description="商品")
public class CesShopGoods implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
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
	@Excel(name = "商品编码", width = 15)
    @ApiModelProperty(value = "商品编码")
    private java.lang.String code;
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
}
