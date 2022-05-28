package com.nbcio.modules.demo.test.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.demo.test.entity.JeecgOrderTicket;

/**
 * @Description: 订单机票
 * @Author: nbacheng
 * @Date:  2019-02-15
 * @Version: V1.0
 */
public interface IJeecgOrderTicketService extends IService<JeecgOrderTicket> {
	
	public List<JeecgOrderTicket> selectTicketsByMainId(String mainId);
}
