package com.nbcio.modules.demo.test.service.impl;

import com.nbcio.modules.demo.test.entity.CesShopGoods;
import com.nbcio.modules.demo.test.mapper.CesShopGoodsMapper;
import com.nbcio.modules.demo.test.service.ICesShopGoodsService;
import com.nbcio.modules.demo.test.vo.CesShopGoodsVo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商品
 * @Author: nbacheng
 * @Date:   2022-05-15
 * @Version: V1.0
 */
@Service
public class CesShopGoodsServiceImpl extends ServiceImpl<CesShopGoodsMapper, CesShopGoods> implements ICesShopGoodsService {

	@Autowired
	private CesShopGoodsMapper cesShopGoodsMapper;
	@Override
	public List<CesShopGoodsVo> getByIds(String ids) {
		// TODO Auto-generated method stub
		 String [] idArray=ids.split(",");
	     return  cesShopGoodsMapper.getByIds(idArray);
	}

} 
