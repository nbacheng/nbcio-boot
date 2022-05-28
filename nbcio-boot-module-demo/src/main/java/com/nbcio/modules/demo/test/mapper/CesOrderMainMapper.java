package com.nbcio.modules.demo.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbcio.modules.demo.test.entity.CesOrderMain;

/**
 * @Description: 商城订单表
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
public interface CesOrderMainMapper extends BaseMapper<CesOrderMain> {
	 IPage<CesOrderMain> myPage(Page<CesOrderMain> page, @Param(Constants.WRAPPER) QueryWrapper<CesOrderMain> queryWrapper);
}
