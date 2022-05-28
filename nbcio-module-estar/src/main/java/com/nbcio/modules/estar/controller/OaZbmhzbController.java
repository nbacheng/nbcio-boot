package com.nbcio.modules.estar.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;

import com.nbcio.modules.estar.entity.OaZbmhzb;
import com.nbcio.modules.estar.service.IOaZbmhzbService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: oa_zbmhzb
 * @Author: nbacheng
 * @Date:   2022-02-24
 * @Version: V1.0
 */
@Api(tags="OA子部门薪资汇总表")
@RestController
@RequestMapping("/estar/oaZbmhzb")
@Slf4j
public class OaZbmhzbController extends JeecgController<OaZbmhzb, IOaZbmhzbService> {
	@Autowired
	private IOaZbmhzbService oaZbmhzbService;
	
	
	/**
	 * 根据子部门查询薪资汇总表
	 *
	 * @param oaZbmhzb
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "oa_zbmhzb-子部门薪资汇总表")
	@ApiOperation(value="oa_zbmhzb-子部门薪资汇总表", notes="oa_zbmhzb-子部门薪资汇总表")
	@GetMapping(value = "/listtotalbysubdep")
	public Result<?> getTotalSalaryBySubDep( @RequestParam(name="salaytime") String salaytime,
								   @RequestParam(name="bmbh") String bmbh,
								   HttpServletRequest req) {
		List<OaZbmhzb> oaZbmhzbList = oaZbmhzbService.getTotalSalaryBySubDep(salaytime, bmbh);
		return Result.OK(oaZbmhzbList);
	}
	

    /**
    * 导出excel
    *
    * @param request
    * @param oaZbmhzb
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, OaZbmhzb oaZbmhzb) {
        return super.exportXls(request, oaZbmhzb, OaZbmhzb.class, "oa_zbmhzb");
    }

}
