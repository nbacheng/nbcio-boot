package com.nbcio.modules.estar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.nbcio.modules.estar.vo.OaSalaryTotal;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;


/**
 * @Description: OA工资表
 * @Author: nbacheng
 * @Date:   2022-05-12
 * @Version: V1.0
 */
public interface OaSalaryTotalMapper extends BaseMapper<OaSalaryTotal> {
	//salarytime就是年月的字符串,MybatisPlus版本为3.4.0及以上，多租户屏蔽接口，否则这个sql出现错误 nbacheng
	//@InterceptorIgnore(tenantLine = "true")
	public List<OaSalaryTotal> getTotalSalaryBySubDep(@Param("salaryyear") Integer salaryyear, @Param("salarymonth") Integer salarymonth, @Param("depno") String depno);
}
