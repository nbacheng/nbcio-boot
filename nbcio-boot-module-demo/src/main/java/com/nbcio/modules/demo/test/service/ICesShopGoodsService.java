package com.nbcio.modules.demo.test.service;

import com.nbcio.modules.demo.test.entity.CesShopGoods;
import com.nbcio.modules.demo.test.vo.CesShopGoodsVo;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 商品
 * @Author: nbacheng
 * @Date:   2022-05-15
 * @Version: V1.0
 */
public interface ICesShopGoodsService extends IService<CesShopGoods> {

	List<CesShopGoodsVo> getByIds(String ids);

}
