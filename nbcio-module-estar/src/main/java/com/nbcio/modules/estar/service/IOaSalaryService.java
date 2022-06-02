package com.nbcio.modules.estar.service;


import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.estar.entity.OaSalary;
import com.nbcio.modules.estar.util.ServiceResult;
import com.nbcio.modules.estar.vo.OaSalaryTotal;

/**
 * @Description: OA工资表
 * @Author: nbacheng
 * @Date:   2022-05-12
 * @Version: V1.0
 */
public interface IOaSalaryService extends IService<OaSalary> {

	IPage<OaSalary> oaSalaryPageList(Page<OaSalary> page, QueryWrapper<OaSalary> queryWrapper);

	List<OaSalary> getSalaryByDep(Integer salaryyear, Integer salarymonth, String depno);

	ServiceResult<String> salaryApprove(Integer salaryyear, Integer salarymonth, String depno);
	
}
