package com.nbcio.modules.demo.test.service.impl;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.demo.test.entity.CesOrderGoods;
import com.nbcio.modules.demo.test.mapper.CesOrderGoodsMapper;
import com.nbcio.modules.demo.test.service.ICesOrderGoodsService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 订单商品
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
@Service
public class CesOrderGoodsServiceImpl extends ServiceImpl<CesOrderGoodsMapper, CesOrderGoods> implements ICesOrderGoodsService {
	
	@Autowired
	private CesOrderGoodsMapper cesOrderGoodsMapper;
	
	@Override
	public List<CesOrderGoods> selectByMainId(String mainId) {
		return cesOrderGoodsMapper.selectByMainId(mainId);
	}
}
