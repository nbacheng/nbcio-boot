package com.nbcio.modules.demo.test.service.impl;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.demo.test.entity.CesOrderCustomer;
import com.nbcio.modules.demo.test.mapper.CesOrderCustomerMapper;
import com.nbcio.modules.demo.test.service.ICesOrderCustomerService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 订单客户
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
@Service
public class CesOrderCustomerServiceImpl extends ServiceImpl<CesOrderCustomerMapper, CesOrderCustomer> implements ICesOrderCustomerService {
	
	@Autowired
	private CesOrderCustomerMapper cesOrderCustomerMapper;
	
	@Override
	public List<CesOrderCustomer> selectByMainId(String mainId) {
		return cesOrderCustomerMapper.selectByMainId(mainId);
	}
}
