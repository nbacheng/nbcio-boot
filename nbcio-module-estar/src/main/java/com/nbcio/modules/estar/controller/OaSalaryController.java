package com.nbcio.modules.estar.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import com.nbcio.modules.estar.entity.OaSalary;
import com.nbcio.modules.estar.service.IOaSalaryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: OA工资表
 * @Author: nbacheng
 * @Date:   2022-05-12
 * @Version: V1.0
 */
@Api(tags="OA工资表")
@RestController
@RequestMapping("/estar/oaSalary")
@Slf4j
public class OaSalaryController extends JeecgController<OaSalary, IOaSalaryService> {
	@Autowired
	private IOaSalaryService oaSalaryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param oaSalary
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "OA工资表-分页列表查询")
	@ApiOperation(value="OA工资表-分页列表查询", notes="OA工资表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OaSalary oaSalary,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OaSalary> queryWrapper = QueryGenerator.initQueryWrapper(oaSalary, req.getParameterMap());
		Page<OaSalary> page = new Page<OaSalary>(pageNo, pageSize);
		IPage<OaSalary> pageList = oaSalaryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param oaSalary
	 * @return
	 */
	@AutoLog(value = "OA工资表-添加")
	@ApiOperation(value="OA工资表-添加", notes="OA工资表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OaSalary oaSalary) {
		oaSalaryService.save(oaSalary);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param oaSalary
	 * @return
	 */
	@AutoLog(value = "OA工资表-编辑")
	@ApiOperation(value="OA工资表-编辑", notes="OA工资表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OaSalary oaSalary) {
		oaSalaryService.updateById(oaSalary);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "OA工资表-通过id删除")
	@ApiOperation(value="OA工资表-通过id删除", notes="OA工资表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		oaSalaryService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "OA工资表-批量删除")
	@ApiOperation(value="OA工资表-批量删除", notes="OA工资表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.oaSalaryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "OA工资表-通过id查询")
	@ApiOperation(value="OA工资表-通过id查询", notes="OA工资表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		OaSalary oaSalary = oaSalaryService.getById(id);
		if(oaSalary==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(oaSalary);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param oaSalary
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, OaSalary oaSalary) {
        return super.exportXls(request, oaSalary, OaSalary.class, "OA工资表");
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
        return super.importExcel(request, response, OaSalary.class);
    }

}
