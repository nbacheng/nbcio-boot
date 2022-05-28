package com.nbcio.modules.flowable.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import com.nbcio.modules.flowable.domain.vo.CustomFormVo;
import com.nbcio.modules.flowable.entity.SysCustomForm;
import com.nbcio.modules.flowable.service.ISysCustomFormService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 系统自定义表单表
 * @Author: nbacheng
 * @Date:   2022-04-23
 * @Version: V1.0
 */
@Api(tags="系统自定义表单表")
@RestController
@RequestMapping("/flowable/sysCustomForm")
@Slf4j
public class SysCustomFormController extends JeecgController<SysCustomForm, ISysCustomFormService> {
	@Autowired
	private ISysCustomFormService sysCustomFormService;
	
	/**
	 * 分页列表查询
	 *
	 * @param sysCustomForm
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "系统自定义表单表-分页列表查询")
	@ApiOperation(value="系统自定义表单表-分页列表查询", notes="系统自定义表单表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysCustomForm sysCustomForm,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SysCustomForm> queryWrapper = QueryGenerator.initQueryWrapper(sysCustomForm, req.getParameterMap());
		Page<SysCustomForm> page = new Page<SysCustomForm>(pageNo, pageSize);
		IPage<SysCustomForm> pageList = sysCustomFormService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param sysCustomForm
	 * @return
	 */
	@AutoLog(value = "系统自定义表单表-添加")
	@ApiOperation(value="系统自定义表单表-添加", notes="系统自定义表单表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysCustomForm sysCustomForm) {
		sysCustomFormService.save(sysCustomForm);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param sysCustomForm
	 * @return
	 */
	@AutoLog(value = "系统自定义表单表-编辑")
	@ApiOperation(value="系统自定义表单表-编辑", notes="系统自定义表单表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysCustomForm sysCustomForm) {
		sysCustomFormService.updateById(sysCustomForm);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *  更新 选择流程定义表更新关联流程信息
	 *
	 * @param sysCustomForm
	 * @return
	 */
	@AutoLog(value = "系统自定义表单表-更新")
	@ApiOperation(value="系统自定义表单表-更新", notes="系统自定义表单表-更新")
	@PostMapping(value = "/updateCustom")
	public Result<?> updateCustom(@RequestBody CustomFormVo customFormVo) {
		sysCustomFormService.updateCustom(customFormVo);
		return Result.OK("关联流程成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "系统自定义表单表-通过id删除")
	@ApiOperation(value="系统自定义表单表-通过id删除", notes="系统自定义表单表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysCustomFormService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "系统自定义表单表-批量删除")
	@ApiOperation(value="系统自定义表单表-批量删除", notes="系统自定义表单表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysCustomFormService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "系统自定义表单表-通过id查询")
	@ApiOperation(value="系统自定义表单表-通过id查询", notes="系统自定义表单表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SysCustomForm sysCustomForm = sysCustomFormService.getById(id);
		if(sysCustomForm==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sysCustomForm);
	}

	/**
     * 获取流程自定义表单详细信息
     *  @param id
     */
	
	@AutoLog(value = "系统流程自定义表单-通过id查询")
	@ApiOperation(value="系统流程自定义表单-通过id查询", notes="系统流程自定义表单-通过id查询")
    @GetMapping(value = "/{formId}")
    public Result<?> getInfo(@PathVariable("formId")  String formId) {
		SysCustomForm sysCustomForm = sysCustomFormService.selectSysCustomFormById(formId);
		if(sysCustomForm==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sysCustomForm);
    }
	

	/**
     * 获取流程自定义表单详细信息
     *  @param serviceName
     */
	/*
	@AutoLog(value = "系统流程自定义表单-通过serviceName查询")
	@ApiOperation(value="系统流程自定义表单-通过serviceName查询", notes="系统流程自定义表单-通过serviceName查询")
    @GetMapping(value = "/{serviceName}")
    public Result<?> getInfoByServiceName(@PathVariable("serviceName")  String serviceName) {
		SysCustomForm sysCustomForm = sysCustomFormService.selectSysCustomFormByServiceName(serviceName);
		if(sysCustomForm==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(sysCustomForm);
    }*/
	
    /**
    * 导出excel
    *
    * @param request
    * @param sysCustomForm
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysCustomForm sysCustomForm) {
        return super.exportXls(request, sysCustomForm, SysCustomForm.class, "系统自定义表单表");
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
        return super.importExcel(request, response, SysCustomForm.class);
    }

}
