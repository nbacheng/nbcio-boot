package com.nbcio.modules.demo.test.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.demo.test.entity.JeecgOrderCustomer;

/**
 * @Description: 订单客户
 * @Author: nbacheng
 * @Date:  2019-02-15
 * @Version: V1.0
 */
public interface IJeecgOrderCustomerService extends IService<JeecgOrderCustomer> {
	
	public List<JeecgOrderCustomer> selectCustomersByMainId(String mainId);
}
