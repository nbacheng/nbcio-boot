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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;

import com.nbcio.modules.estar.entity.OaSalaryapproval;
import com.nbcio.modules.estar.service.IOaSalaryapprovalService;
import com.nbcio.modules.estar.vo.ProcessUpdateVo;

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
 * @Description: 薪资审批表
 * @Author: nbacheng
 * @Date:   2022-03-03
 * @Version: V1.0
 */
@Api(tags="薪资审批表")
@RestController
@RequestMapping("/estar/oaSalaryapproval")
@Slf4j
public class OaSalaryapprovalController extends JeecgController<OaSalaryapproval, IOaSalaryapprovalService> {
	@Autowired
	private IOaSalaryapprovalService oaSalaryapprovalService;
	
	/**
	 * 分页列表查询
	 *
	 * @param oaSalaryapproval
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "薪资审批表-分页列表查询")
	@ApiOperation(value="薪资审批表-分页列表查询", notes="薪资审批表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OaSalaryapproval oaSalaryapproval,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OaSalaryapproval> queryWrapper = QueryGenerator.initQueryWrapper(oaSalaryapproval, req.getParameterMap());
		Page<OaSalaryapproval> page = new Page<OaSalaryapproval>(pageNo, pageSize);
		IPage<OaSalaryapproval> pageList = oaSalaryapprovalService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param oaSalaryapproval
	 * @return
	 */
	@AutoLog(value = "薪资审批表-添加")
	@ApiOperation(value="薪资审批表-添加", notes="薪资审批表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OaSalaryapproval oaSalaryapproval) {
		oaSalaryapprovalService.save(oaSalaryapproval);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param oaSalaryapproval
	 * @return
	 */
	@AutoLog(value = "薪资审批表-编辑")
	@ApiOperation(value="薪资审批表-编辑", notes="薪资审批表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OaSalaryapproval oaSalaryapproval) {
		oaSalaryapprovalService.updateById(oaSalaryapproval);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "薪资审批表-通过id删除")
	@ApiOperation(value="薪资审批表-通过id删除", notes="薪资审批表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		oaSalaryapprovalService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "薪资审批表-批量删除")
	@ApiOperation(value="薪资审批表-批量删除", notes="薪资审批表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.oaSalaryapprovalService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "薪资审批表-通过id查询")
	@ApiOperation(value="薪资审批表-通过id查询", notes="薪资审批表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		OaSalaryapproval oaSalaryapproval = oaSalaryapprovalService.getById(id);
		if(oaSalaryapproval==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(oaSalaryapproval);
	}
	
	/**
	 * 通过processInstanceId查询
	 *
	 * @param processInstanceId
	 * @return
	 */
	@AutoLog(value = "薪资审批表-通过processInstanceId查询")
	@ApiOperation(value="薪资审批表-通过processInstanceId查询", notes="薪资审批表-通过processInstanceId查询")
	@GetMapping(value = "/queryByInstanceId")
	public Result<?> queryByInstanceId(@RequestParam(name="processInstanceId",required=true) String processInstanceId) {
		OaSalaryapproval oaSalaryapproval = oaSalaryapprovalService.getByInstanceId(processInstanceId);
		if(oaSalaryapproval==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(oaSalaryapproval);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param oaSalaryapproval
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, OaSalaryapproval oaSalaryapproval) {
        return super.exportXls(request, oaSalaryapproval, OaSalaryapproval.class, "薪资审批表");
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
        return super.importExcel(request, response, OaSalaryapproval.class);
    }
    
    
    /**
     * 根据流程实例id更新流程状态
   *
   * @param ProcessUpdateVo
   * @return
   */
   @RequestMapping(value = "/updateProcessStatus", method = RequestMethod.POST)
   public Result<?> updateProcessStatus(@RequestBody ProcessUpdateVo processUpdateVo) {
	   oaSalaryapprovalService.updateProcessStatus(processUpdateVo);
       return Result.OK("更新流程状态成功！");
   }
    

}
