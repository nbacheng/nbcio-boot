package com.nbcio.modules.estar.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;

import com.nbcio.modules.estar.config.Constant;
import com.nbcio.modules.estar.config.URLConstant;
import com.nbcio.modules.estar.entity.OaGzspb;
import com.nbcio.modules.estar.entity.OaSubdep;
import com.nbcio.modules.estar.entity.OaZbmhzb;
import com.nbcio.modules.estar.model.ProcessInstanceInputVO;
import com.nbcio.modules.estar.service.IOaGzspbService;
import com.nbcio.modules.estar.service.IOaSubdepService;
import com.nbcio.modules.estar.service.IOaZbmhzbService;
import com.nbcio.modules.estar.service.impl.OaGzspbServiceImpl;
import com.nbcio.modules.estar.util.AccessTokenUtil;
import com.nbcio.modules.estar.util.LogFormatter;
import com.nbcio.modules.estar.util.ServiceResult;
import com.nbcio.modules.estar.util.ServiceResultCode;
import com.nbcio.modules.estar.util.LogFormatter.LogEvent;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: oa_gzspb OA钉钉工资审批数据接口
 * @Author: nbacheng
 * @Date: 2022-02-18
 * @Version: V1.0
 */
@Api(tags = "OA钉钉工资审批数据接口")
@RestController
@RequestMapping("/estar/oaGzspb")
@Slf4j
public class OaGzspbController  {
	//private static final Logger bizLogger = LoggerFactory.getLogger(ProcessinstanceController.class);
	@Autowired
	private IOaGzspbService  oaGzspbService;
	@Autowired
	private IOaSubdepService oaSubdepService;
	@Autowired
	private IOaZbmhzbService oaZbmhzbService;
	@Autowired
	OaGzspbServiceImpl oaGzspbServiceImpl;
	
	
	/**
	 * 分页列表查询
	 *
	 * @param oaGzspb
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */

	@AutoLog(value = "oa_gzspb-分页薪资数据")
	@ApiOperation(value="oa_gzspb-分页薪资数据", notes="oa_gzspb-分页薪资数据")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OaGzspb oaGzspb,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OaGzspb> queryWrapper = QueryGenerator.initQueryWrapper(oaGzspb, req.getParameterMap());
		Page<OaGzspb> page = new Page<OaGzspb>(pageNo, pageSize);
		IPage<OaGzspb> pageList = oaGzspbService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 根据年月部门获取薪资审批数据
	 *
	 * @param oaGzspb
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "oa_gzspb-薪资审批数据")
	@ApiOperation(value = "oa_gzspb-薪资审批数据", notes = "oa_gzspb-薪资审批数据")
	@GetMapping(value = "/listsalarybydep")
	public Result<?> getSalaryByDep(@RequestParam(name = "salaryyear") String salaryyear,
			@RequestParam(name = "salarymonth") String salarymonth, @RequestParam(name = "depno") String depno) {

		List<OaGzspb> oaGzspbList = oaGzspbService.getSalaryByDep(salaryyear, salarymonth, depno);
		return Result.OK(oaGzspbList);
	}
	
	/**
	 * 根据年月部门获取子部门的薪资汇总数据
	 *
	 * @param oaGzspb
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "oa_gzspb-获取子部门的薪资汇总数据")
	@ApiOperation(value = "oa_gzspb-获取子部门的薪资汇总数据", notes = "oa_gzspb-获取子部门的薪资汇总数据")
	@GetMapping(value = "/listtotalbysubdep")
	public Result<?> getTotalBySubDep(@RequestParam(name = "salaryyear") String salaryyear,
			@RequestParam(name = "salarymonth") String salarymonth, @RequestParam(name = "upperno") String upperno) {

		String salarytime = salaryyear + salarymonth;
		List<OaSubdep> oaSubdepList = oaSubdepService.getSubDep(upperno);
		List<OaZbmhzb> oaZbmhzbList = new ArrayList<OaZbmhzb>();
		if (oaSubdepList.size() > 0 ) {
			for (int i = 0; i < oaSubdepList.size(); i++) {
				oaZbmhzbList.add(oaZbmhzbService.getTotalSalaryBySubDep(salarytime, oaSubdepList.get(i).getDepno()).get(0));
			}
			return Result.OK(oaZbmhzbList);
		}
		return null;		
	}

	/**
	 * 根据年月部门获取薪资审批数据并提交钉钉审批
	 *
	 * @param salaryyear,
	 *            salarymonth, depno
	 * @param req
	 * @return
	 */
	@AutoLog(value = "oa_gzspb-薪资数据钉钉审批")
	@ApiOperation(value = "oa_gzspb-薪资审批钉钉审批", notes = "oa_gzspb-薪资数据钉钉审批")
	@PostMapping(value = "/salaryapprove")
	public ServiceResult<String> salaryapprove(@RequestBody JSONObject jsonObject) {
		String salaryyear = jsonObject.getString("salaryyear");
		String salarymonth = jsonObject.getString("salarymonth");
		String depno = jsonObject.getString("depno");
		return oaGzspbServiceImpl.SalaryApprove(salaryyear, salarymonth, depno);      
	}

}
