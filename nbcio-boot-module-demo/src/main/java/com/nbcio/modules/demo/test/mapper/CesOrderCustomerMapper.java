package com.nbcio.modules.demo.test.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.demo.test.entity.CesOrderCustomer;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: 订单客户
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
public interface CesOrderCustomerMapper extends BaseMapper<CesOrderCustomer> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<CesOrderCustomer> selectByMainId(@Param("mainId") String mainId);
}
