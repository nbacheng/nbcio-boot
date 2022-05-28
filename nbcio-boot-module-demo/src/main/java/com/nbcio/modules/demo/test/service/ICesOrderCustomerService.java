package com.nbcio.modules.demo.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.demo.test.entity.CesOrderCustomer;

import java.util.List;

/**
 * @Description: 订单客户
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
public interface ICesOrderCustomerService extends IService<CesOrderCustomer> {

	public List<CesOrderCustomer> selectByMainId(String mainId);
}
