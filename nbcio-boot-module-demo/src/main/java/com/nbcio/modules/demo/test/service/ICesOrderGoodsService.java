package com.nbcio.modules.demo.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.demo.test.entity.CesOrderGoods;

import java.util.List;

/**
 * @Description: 订单商品
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
public interface ICesOrderGoodsService extends IService<CesOrderGoods> {

	public List<CesOrderGoods> selectByMainId(String mainId);
}
