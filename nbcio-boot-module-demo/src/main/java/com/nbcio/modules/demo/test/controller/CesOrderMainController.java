package com.nbcio.modules.demo.test.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.FillRuleConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.FillRuleUtil;
import org.jeecg.common.util.oConvertUtils;
import com.nbcio.modules.demo.test.entity.CesOrderCustomer;
import com.nbcio.modules.demo.test.entity.CesOrderGoods;
import com.nbcio.modules.demo.test.entity.CesOrderMain;
import com.nbcio.modules.demo.test.service.ICesOrderCustomerService;
import com.nbcio.modules.demo.test.service.ICesOrderGoodsService;
import com.nbcio.modules.demo.test.service.ICesOrderMainService;
import com.nbcio.modules.demo.test.vo.CesOrderMainPage;

import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 商城订单表
 * @Author: nbacheng
 * @Date:   2022-04-25
 * @Version: V1.0
 */
@Api(tags="商城订单表")
@RestController
@RequestMapping("/demo/test/cesOrderMain")
@Slf4j
public class CesOrderMainController {
	@Autowired
	private ICesOrderMainService cesOrderMainService;
	@Autowired
	private ICesOrderGoodsService cesOrderGoodsService;
	@Autowired
	private ICesOrderCustomerService cesOrderCustomerService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cesOrderMain
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "商城订单表-分页列表查询")
	@ApiOperation(value="商城订单表-分页列表查询", notes="商城订单表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CesOrderMain cesOrderMain,
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
		
		QueryWrapper<CesOrderMain> queryWrapper = QueryGenerator.initQueryWrapper(cesOrderMain, ParameterMap);
		Page<CesOrderMain> page = new Page<CesOrderMain>(pageNo, pageSize);
		//IPage<CesOrderMain> pageList = cesOrderMainService.page(page, queryWrapper);
		IPage<CesOrderMain> pageList = cesOrderMainService.myPage(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cesOrderMainPage
	 * @return
	 */
	@AutoLog(value = "商城订单表-添加")
	@ApiOperation(value="商城订单表-添加", notes="商城订单表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CesOrderMainPage cesOrderMainPage) {
		CesOrderMain cesOrderMain = new CesOrderMain();
		BeanUtils.copyProperties(cesOrderMainPage, cesOrderMain);
		//String ordercode = (String) FillRuleUtil.executeRule(FillRuleConstant.DEMO_ORDER,null);
		//cesOrderMain.setOrderCode(ordercode);
		cesOrderMainService.saveMain(cesOrderMain, cesOrderMainPage.getCesOrderGoodsList(),cesOrderMainPage.getCesOrderCustomerList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cesOrderMainPage
	 * @return
	 */
	@AutoLog(value = "商城订单表-编辑")
	@ApiOperation(value="商城订单表-编辑", notes="商城订单表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CesOrderMainPage cesOrderMainPage) {
		CesOrderMain cesOrderMain = new CesOrderMain();
		BeanUtils.copyProperties(cesOrderMainPage, cesOrderMain);
		CesOrderMain cesOrderMainEntity = cesOrderMainService.getById(cesOrderMain.getId());
		if(cesOrderMainEntity==null) {
			return Result.error("未找到对应数据");
		}
		cesOrderMainService.updateMain(cesOrderMain, cesOrderMainPage.getCesOrderGoodsList(),cesOrderMainPage.getCesOrderCustomerList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商城订单表-通过id删除")
	@ApiOperation(value="商城订单表-通过id删除", notes="商城订单表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		cesOrderMainService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "商城订单表-批量删除")
	@ApiOperation(value="商城订单表-批量删除", notes="商城订单表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cesOrderMainService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商城订单表-通过id查询")
	@ApiOperation(value="商城订单表-通过id查询", notes="商城订单表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CesOrderMain cesOrderMain = cesOrderMainService.getById(id);
		if(cesOrderMain==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cesOrderMain);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "订单商品通过主表ID查询")
	@ApiOperation(value="订单商品主表ID查询", notes="订单商品-通主表ID查询")
	@GetMapping(value = "/queryCesOrderGoodsByMainId")
	public Result<?> queryCesOrderGoodsListByMainId(@RequestParam(name="id",required=true) String id) {
		List<CesOrderGoods> cesOrderGoodsList = cesOrderGoodsService.selectByMainId(id);
		return Result.OK(cesOrderGoodsList);
	}
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "订单客户通过主表ID查询")
	@ApiOperation(value="订单客户主表ID查询", notes="订单客户-通主表ID查询")
	@GetMapping(value = "/queryCesOrderCustomerByMainId")
	public Result<?> queryCesOrderCustomerListByMainId(@RequestParam(name="id",required=true) String id) {
		List<CesOrderCustomer> cesOrderCustomerList = cesOrderCustomerService.selectByMainId(id);
		return Result.OK(cesOrderCustomerList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cesOrderMain
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CesOrderMain cesOrderMain) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<CesOrderMain> queryWrapper = QueryGenerator.initQueryWrapper(cesOrderMain, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //Step.2 获取导出数据
      List<CesOrderMain> queryList = cesOrderMainService.list(queryWrapper);
      // 过滤选中数据
      String selections = request.getParameter("selections");
      List<CesOrderMain> cesOrderMainList = new ArrayList<CesOrderMain>();
      if(oConvertUtils.isEmpty(selections)) {
          cesOrderMainList = queryList;
      }else {
          List<String> selectionList = Arrays.asList(selections.split(","));
          cesOrderMainList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
      }

      // Step.3 组装pageList
      List<CesOrderMainPage> pageList = new ArrayList<CesOrderMainPage>();
      for (CesOrderMain main : cesOrderMainList) {
          CesOrderMainPage vo = new CesOrderMainPage();
          BeanUtils.copyProperties(main, vo);
          List<CesOrderGoods> cesOrderGoodsList = cesOrderGoodsService.selectByMainId(main.getId());
          vo.setCesOrderGoodsList(cesOrderGoodsList);
          List<CesOrderCustomer> cesOrderCustomerList = cesOrderCustomerService.selectByMainId(main.getId());
          vo.setCesOrderCustomerList(cesOrderCustomerList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "商城订单表列表");
      mv.addObject(NormalExcelConstants.CLASS, CesOrderMainPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("商城订单表数据", "导出人:"+sysUser.getRealname(), "商城订单表"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
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
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<CesOrderMainPage> list = ExcelImportUtil.importExcel(file.getInputStream(), CesOrderMainPage.class, params);
              for (CesOrderMainPage page : list) {
                  CesOrderMain po = new CesOrderMain();
                  BeanUtils.copyProperties(page, po);
                  cesOrderMainService.saveMain(po, page.getCesOrderGoodsList(),page.getCesOrderCustomerList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.OK("文件导入失败！");
    }

}
