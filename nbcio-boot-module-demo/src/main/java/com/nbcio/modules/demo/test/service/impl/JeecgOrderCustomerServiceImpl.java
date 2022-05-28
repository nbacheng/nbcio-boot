package com.nbcio.modules.demo.test.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbcio.modules.demo.test.entity.JeecgOrderCustomer;
import com.nbcio.modules.demo.test.mapper.JeecgOrderCustomerMapper;
import com.nbcio.modules.demo.test.service.IJeecgOrderCustomerService;

/**
 * @Description: 订单客户
 * @Author: nbacheng
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Service
public class JeecgOrderCustomerServiceImpl extends ServiceImpl<JeecgOrderCustomerMapper, JeecgOrderCustomer> implements IJeecgOrderCustomerService {

	@Autowired
	private JeecgOrderCustomerMapper jeecgOrderCustomerMapper;
	
	@Override
	public List<JeecgOrderCustomer> selectCustomersByMainId(String mainId) {
		return jeecgOrderCustomerMapper.selectCustomersByMainId(mainId);
	}

}
