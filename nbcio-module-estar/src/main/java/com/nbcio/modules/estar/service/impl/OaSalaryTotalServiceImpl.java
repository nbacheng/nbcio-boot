package com.nbcio.modules.estar.service.impl;

import com.nbcio.modules.estar.mapper.OaSalaryTotalMapper;
import com.nbcio.modules.estar.service.IOaSalaryTotalService;
import com.nbcio.modules.estar.vo.OaSalaryTotal;
import com.nbcio.modules.flowable.service.impl.FlowDefinitionServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: OA工资表
 * @Author: nbacheng
 * @Date:   2022-05-12
 * @Version: V1.0
 */
@Service
public class OaSalaryTotalServiceImpl extends ServiceImpl<OaSalaryTotalMapper, OaSalaryTotal> implements IOaSalaryTotalService {

	@Autowired
	OaSalaryTotalMapper oaSalaryTotalMapper;
	

	
	@Autowired	
	FlowDefinitionServiceImpl flowDefinitionServiceImpl;
	
	@Override
	public List<OaSalaryTotal> getTotalSalaryBySubDep(Integer salaryyear, Integer salarymonth, String depno) {
		List<OaSalaryTotal> oaSalaryTotalList = oaSalaryTotalMapper.getTotalSalaryBySubDep(salaryyear, salarymonth, depno);
		return oaSalaryTotalList;
	}

	
}
