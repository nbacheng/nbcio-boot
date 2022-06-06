package com.nbcio.modules.estar.service;


import java.util.List;


import com.baomidou.mybatisplus.extension.service.IService;

import com.nbcio.modules.estar.vo.OaSalaryTotal;

/**
 * @Description: OA工资表
 * @Author: nbacheng
 * @Date:   2022-05-12
 * @Version: V1.0
 */
public interface IOaSalaryTotalService extends IService<OaSalaryTotal> {

	 List<OaSalaryTotal> getTotalSalaryBySubDep(Integer salaryyear, Integer salarymonth, String depno);

}
