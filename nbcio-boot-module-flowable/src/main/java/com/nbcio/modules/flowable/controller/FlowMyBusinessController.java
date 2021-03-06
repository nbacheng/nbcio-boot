package com.nbcio.modules.flowable.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import com.nbcio.modules.flowable.apithird.business.entity.FlowMyBusiness;
import com.nbcio.modules.flowable.apithird.business.service.IFlowMyBusinessService;

import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: flow_my_business
 * @Author: nbacheng
 * @Date:   2022-04-26
 * @Version: V1.0
 */
@Api(tags="flow_my_business")
@RestController
@RequestMapping("/flowable/flowMyBusiness")
@Slf4j
public class FlowMyBusinessController extends JeecgController<FlowMyBusiness, IFlowMyBusinessService> {
	@Autowired
	private IFlowMyBusinessService flowMyBusinessService;
	
	/**
	 * ??????????????????
	 *
	 * @param flowMyBusiness
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "flow_my_business-??????????????????")
	@ApiOperation(value="flow_my_business-??????????????????", notes="flow_my_business-??????????????????")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(FlowMyBusiness flowMyBusiness,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FlowMyBusiness> queryWrapper = QueryGenerator.initQueryWrapper(flowMyBusiness, req.getParameterMap());
		Page<FlowMyBusiness> page = new Page<FlowMyBusiness>(pageNo, pageSize);
		IPage<FlowMyBusiness> pageList = flowMyBusinessService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   ??????
	 *
	 * @param flowMyBusiness
	 * @return
	 */
	@AutoLog(value = "flow_my_business-??????")
	@ApiOperation(value="flow_my_business-??????", notes="flow_my_business-??????")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody FlowMyBusiness flowMyBusiness) {
		flowMyBusinessService.save(flowMyBusiness);
		return Result.OK("???????????????");
	}
	
	/**
	 *  ??????
	 *
	 * @param flowMyBusiness
	 * @return
	 */
	@AutoLog(value = "flow_my_business-??????")
	@ApiOperation(value="flow_my_business-??????", notes="flow_my_business-??????")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody FlowMyBusiness flowMyBusiness) {
		flowMyBusinessService.updateById(flowMyBusiness);
		return Result.OK("????????????!");
	}
	
	/**
	 *   ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "flow_my_business-??????id??????")
	@ApiOperation(value="flow_my_business-??????id??????", notes="flow_my_business-??????id??????")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		flowMyBusinessService.removeById(id);
		return Result.OK("????????????!");
	}
	
	/**
	 *  ????????????
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "flow_my_business-????????????")
	@ApiOperation(value="flow_my_business-????????????", notes="flow_my_business-????????????")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.flowMyBusinessService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("??????????????????!");
	}
	
	/**
	 * ??????id??????
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "flow_my_business-??????id??????")
	@ApiOperation(value="flow_my_business-??????id??????", notes="flow_my_business-??????id??????")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		FlowMyBusiness flowMyBusiness = flowMyBusinessService.getById(id);
		if(flowMyBusiness==null) {
			return Result.error("?????????????????????");
		}
		return Result.OK(flowMyBusiness);
	}

    /**
    * ??????excel
    *
    * @param request
    * @param flowMyBusiness
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FlowMyBusiness flowMyBusiness) {
        return super.exportXls(request, flowMyBusiness, FlowMyBusiness.class, "flow_my_business");
    }

    /**
      * ??????excel????????????
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, FlowMyBusiness.class);
    }

}
