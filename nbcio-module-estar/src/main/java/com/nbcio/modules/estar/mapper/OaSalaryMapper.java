package com.nbcio.modules.estar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nbcio.modules.estar.entity.OaSalary;
import com.nbcio.modules.estar.vo.OaSalaryTotal;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: OA工资表
 * @Author: nbacheng
 * @Date:   2022-05-12
 * @Version: V1.0
 */
public interface OaSalaryMapper extends BaseMapper<OaSalary> {
	IPage<OaSalary> oaSalaryPageList(Page<OaSalary> page,  @Param(Constants.WRAPPER) QueryWrapper<OaSalary> queryWrapper);
	public List<OaSalary> getSalaryByDep(@Param("salaryyear") Integer salaryyear, @Param("salarymonth") Integer salarymonth, @Param("depno") String depno);

}
