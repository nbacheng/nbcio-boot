package com.nbcio.modules.demo.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nbcio.modules.demo.test.entity.CesShopGoods;
import com.nbcio.modules.demo.test.vo.CesShopGoodsVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 商品
 * @Author: nbacheng
 * @Date:   2022-05-15
 * @Version: V1.0
 */
public interface CesShopGoodsMapper extends BaseMapper<CesShopGoods> {

	public List<CesShopGoodsVo> getByIds(@Param("idArray") String[] idArray);
	public List<CesShopGoodsVo> getByCodes(@Param("codeArray") String[] codeArray);

}
