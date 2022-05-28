package com.nbcio.modules.demo.test.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.demo.test.entity.CesOrderGoods;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: 订单商品
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
public interface CesOrderGoodsMapper extends BaseMapper<CesOrderGoods> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<CesOrderGoods> selectByMainId(@Param("mainId") String mainId);
}
