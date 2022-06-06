package com.nbcio.modules.estar.controller;

import java.util.ArrayList;

import java.util.List;

import org.jeecg.common.api.vo.Result;

import com.nbcio.modules.estar.entity.OaSubdep;
import com.nbcio.modules.estar.service.IOaSalaryTotalService;
import com.nbcio.modules.estar.service.IOaSubdepService;
import com.nbcio.modules.estar.vo.OaSalaryTotal;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: OA工资表
 * @Author: nbacheng
 * @Date:   2022-05-12
 * @Version: V1.0
 */
@Api(tags = "OA钉钉工资审批数据接口")
@RestController
@RequestMapping("/estar/oaSalaryTotal")
public class OaSalaryTotalController extends JeecgController<OaSalaryTotal, IOaSalaryTotalService> {
	@Autowired
	private IOaSalaryTotalService oaSalaryTotalService;
	@Autowired
	private IOaSubdepService  oaSubdepService;
	
	
	/**
	 * 根据年月部门获取子部门的薪资汇总数据
	 *
	 * @param oaSalaryTotal
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "oa_salarytotal-获取子部门的薪资汇总数据")
	@ApiOperation(value = "oa_salarytotal-获取子部门的薪资汇总数据", notes = "oa_salarytotal-获取子部门的薪资汇总数据")
	@GetMapping(value = "/listtotalbysubdep")
	public Result<?> getTotalBySubDep(@RequestParam(name = "salaryyear") String salaryyear,
			@RequestParam(name = "salarymonth") String salarymonth, @RequestParam(name = "depno") String depno) {

		
		List<OaSubdep> oaSubdepList = oaSubdepService.getSubDep(depno);
		List<OaSalaryTotal> oaSalaryTotalList = new ArrayList<OaSalaryTotal>();
		if (oaSubdepList.size() > 0 ) {
			for (int i = 0; i < oaSubdepList.size(); i++) {
				oaSalaryTotalList.add(oaSalaryTotalService.getTotalSalaryBySubDep(Integer.valueOf(salaryyear),Integer.valueOf(salarymonth), oaSubdepList.get(i).getDepno()).get(0));
			}
			return Result.OK(oaSalaryTotalList);
		}
		return null;		
	}

}
