package com.nbcio.modules.estar.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;

import com.nbcio.modules.estar.entity.OaSubdep;
import com.nbcio.modules.estar.service.IOaSubdepService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: oa_subdep
 * @Author: nbacheng
 * @Date:   2022-02-24
 * @Version: V1.0
 */
@Api(tags="OA部门查询接口")
@RestController
@RequestMapping("/estar/oaSubdep")
@Slf4j
public class OaSubdepController extends JeecgController<OaSubdep, IOaSubdepService> {
	@Autowired
	private IOaSubdepService oaSubdepService;
	
	/**
	 * 部门名称查询
	 *
	 * @param oabdep
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	
	@AutoLog(value = "oa_dep-部门名称查询")
	@ApiOperation(value="oa_dep-部门名称查询", notes="oa_dep-部门名称查询")
	@GetMapping(value = "/listdep")
	public Result<?> GetDep(@RequestParam(name="depno") String depno,
								   HttpServletRequest req) {
		List<OaSubdep> oaDepList = oaSubdepService.getDep(depno);
		return Result.OK(oaDepList);
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
	public Result<?> GetSubDep(@RequestParam(name="upperno") String upperno,
								   HttpServletRequest req) {
		List<OaSubdep> oaSubdepList = oaSubdepService.getSubDep(upperno);
		return Result.OK(oaSubdepList);
	}
	
	/**
	 * 分页列表查询
	 *
	 * @param oaSubdep
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */

	@AutoLog(value = "oa_subdep-分页列表查询")
	@ApiOperation(value="oa_subdep-分页列表查询", notes="oa_subdep-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OaSubdep oaSubdep,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OaSubdep> queryWrapper = QueryGenerator.initQueryWrapper(oaSubdep, req.getParameterMap());
		Page<OaSubdep> page = new Page<OaSubdep>(pageNo, pageSize);
		IPage<OaSubdep> pageList = oaSubdepService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	
    /**
    * 导出excel
    *
    * @param request
    * @param oaSubdep
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, OaSubdep oaSubdep) {
        return super.exportXls(request, oaSubdep, OaSubdep.class, "oa_subdep");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    /*
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, OaSubdep.class);
    }*/

}
