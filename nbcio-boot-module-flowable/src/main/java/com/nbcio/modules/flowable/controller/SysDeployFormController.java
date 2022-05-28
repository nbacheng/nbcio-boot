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
import javax.transaction.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import com.nbcio.modules.flowable.apithird.service.FlowCommonService;
import com.nbcio.modules.flowable.entity.SysCustomForm;
import com.nbcio.modules.flowable.entity.SysDeployForm;
import com.nbcio.modules.flowable.service.ISysDeployFormService;

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
 * @Description: 流程实例关联表单
 * @Author: nbacheng
 * @Date:   2022-04-11
 * @Version: V1.0
 */
@Api(tags="流程实例关联表单")
@RestController
@RequestMapping("/flowable/sysDeployForm")
@Slf4j
public class SysDeployFormController extends JeecgController<SysDeployForm, ISysDeployFormService> {
	@Autowired
	private ISysDeployFormService sysDeployFormService;
	@Autowired
	private FlowCommonService flowCommonService;
	
	/**
	 * 分页列表查询
	 *
	 * @param sysDeployForm
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "流程实例关联表单-分页列表查询")
	@ApiOperation(value="流程实例关联表单-分页列表查询", notes="流程实例关联表单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysDeployForm sysDeployForm,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SysDeployForm> queryWrapper = QueryGenerator.initQueryWrapper(sysDeployForm, req.getParameterMap());
		Page<SysDeployForm> page = new Page<SysDeployForm>(pageNo, pageSize);
		IPage<SysDeployForm> pageList = sysDeployFormService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param sysDeployForm
	 * @return
	 */
	@AutoLog(value = "流程实例关联表单-添加")
	@ApiOperation(value="流程实例关联表单-添加", notes="流程实例关联表单-添加")
	@PostMapping(value = "/add")
	@Transactional
	public Result<?> add(@RequestBody SysDeployForm sysDeployForm) {
		sysDeployFormService.save(sysDeployForm);
		
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param sysDeployForm
	 * @return
	 */
	@AutoLog(value = "流程实例关联表单-编辑")
	@ApiOperation(value="流程实例关联表单-编辑", notes="流程实例关联表单-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysDeployForm sysDeployForm) {
		sysDeployFormService.updateById(sysDeployForm);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "流程实例关联表单-通过id删除")
	@ApiOperation(value="流程实例关联表单-通过id删除", notes="流程实例关联表单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysDeployFormService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "流程实例关联表单-批量删除")
	@ApiOperation(value="流程实例关联表单-批量删除", notes="流程实例关联表单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysDeployFormService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "流程实例关联表单-通过id查询")
	@ApiOperation(value="流程实例关联表单-通过id查询", notes="流程实例关联表单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SysDeployForm sysDeployForm = sysDeployFormService.getById(id);
		if(sysDeployForm==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sysDeployForm);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sysDeployForm
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysDeployForm sysDeployForm) {
        return super.exportXls(request, sysDeployForm, SysDeployForm.class, "流程实例关联表单");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysDeployForm.class);
    }

}
