package com.nbcio.modules.demo.test.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.demo.test.entity.CesOrderCustomer;
import com.nbcio.modules.demo.test.entity.CesOrderGoods;
import com.nbcio.modules.demo.test.entity.CesOrderMain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 商城订单表
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
public interface ICesOrderMainService extends IService<CesOrderMain> {

	 IPage<CesOrderMain> myPage(Page<CesOrderMain> page, QueryWrapper<CesOrderMain> queryWrapper);
	
	/**
	 * 添加一对多
	 * 
	 */
	public void saveMain(CesOrderMain cesOrderMain,List<CesOrderGoods> cesOrderGoodsList,List<CesOrderCustomer> cesOrderCustomerList) ;
	
	/**
	 * 修改一对多
	 * 
	 */
	public void updateMain(CesOrderMain cesOrderMain,List<CesOrderGoods> cesOrderGoodsList,List<CesOrderCustomer> cesOrderCustomerList);
	
	/**
	 * 删除一对多
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
