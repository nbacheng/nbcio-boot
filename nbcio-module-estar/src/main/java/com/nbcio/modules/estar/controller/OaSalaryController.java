package com.nbcio.modules.estar.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.nbcio.modules.estar.entity.OaSalary;
import com.nbcio.modules.estar.entity.OaSubdep;
import com.nbcio.modules.estar.service.IOaSalaryService;
import com.nbcio.modules.estar.service.IOaSubdepService;
import com.nbcio.modules.estar.util.ServiceResult;
import com.nbcio.modules.estar.vo.OaSalaryTotal;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

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
@RequestMapping("/estar/oaSalary")
@Slf4j
public class OaSalaryController extends JeecgController<OaSalary, IOaSalaryService> {
	@Autowired
	private IOaSalaryService oaSalaryService;
	@Autowired
	private IOaSubdepService  oaSubdepService;
	
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
	//by nbacheng for java.sql.SQLSyntaxErrorException: ORA-00918: 未明确定义列  修正oracle版本问题
		Map<String, String[]> ParameterMap = new HashMap<String, String[]>(req.getParameterMap());
		String[] column = new String[]{""};
		if(ParameterMap!=null&&  ParameterMap.containsKey("column")) {
			column[0] = ParameterMap.get("column")[0];
			column[0] = "t."+ column[0];
			ParameterMap.replace("column", column);
			log.info("修改的排序规则>>列:" + ParameterMap.get("column")[0]);			
		}
		QueryWrapper<OaSalary> queryWrapper = QueryGenerator.initQueryWrapper(oaSalary, req.getParameterMap());
		Page<OaSalary> page = new Page<OaSalary>(pageNo, pageSize);
		//IPage<OaSalary> pageList = oaSalaryService.page(page, queryWrapper);
		IPage<OaSalary> pageList = oaSalaryService.oaSalaryPageList(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 根据年月部门获取薪资审批数据
	 *
	 * @param OaSalary
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "oa_salary-薪资审批数据")
	@ApiOperation(value = "oa_salary-薪资审批数据", notes = "oa_salary-薪资审批数据")
	@GetMapping(value = "/listsalarybydep")
	public Result<?> getSalaryByDep(@RequestParam(name = "salaryyear") String salaryyear,
			@RequestParam(name = "salarymonth") String salarymonth, @RequestParam(name = "depno") String depno) {

		List<OaSalary> oaSalaryList = oaSalaryService.getSalaryByDep(Integer.valueOf(salaryyear), Integer.valueOf(salarymonth), depno);
		return Result.OK(oaSalaryList);
	}
	
	/**
	 * 根据年月部门获取薪资审批数据并提交钉钉审批
	 *
	 * @param salaryyear,
	 *            salarymonth, depno
	 * @param req
	 * @return
	 */
	@AutoLog(value = "oa_salarytotal-薪资数据钉钉审批")
	@ApiOperation(value = "oa_salarytotal-薪资审批钉钉审批", notes = "oa_salarytotal-薪资数据钉钉审批")
	@PostMapping(value = "/salaryapprove")
	public ServiceResult<String> salaryapprove(@RequestBody JSONObject jsonObject) {
		String salaryyear = jsonObject.getString("salaryyear");
		String salarymonth = jsonObject.getString("salarymonth");
		String depno = jsonObject.getString("depno");
		return oaSalaryService.salaryApprove(Integer.valueOf(salaryyear), Integer.valueOf(salarymonth), depno);      
	}


	/**
	 * 子部门查询
	 *
	 * @param oaSubdep
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	
	@AutoLog(value = "oa_subdep-子部门查询")
	@ApiOperation(value="oa_subdep-子部门查询", notes="oa_subdep-子部门查询")
	@GetMapping(value = "/listsubdep")
	public Result<?> GetSubDep(@RequestParam(name="depno") String depno,
								   HttpServletRequest req) {
		List<OaSubdep> oaSubdepList = oaSubdepService.getSubDep(depno);
		return Result.OK(oaSubdepList);
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
