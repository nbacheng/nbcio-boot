package com.nbcio.modules.demo.test.controller;

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
import com.nbcio.modules.demo.test.entity.CesShopGoods;
import com.nbcio.modules.demo.test.service.ICesShopGoodsService;
import com.nbcio.modules.demo.test.vo.CesShopGoodsVo;
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
 * @Description: 商品
 * @Author: nbacheng
 * @Date:   2022-05-15
 * @Version: V1.0
 */
@Api(tags="商品")
@RestController
@RequestMapping("/test/cesShopGoods")
@Slf4j
public class CesShopGoodsController extends JeecgController<CesShopGoods, ICesShopGoodsService> {
	@Autowired
	private ICesShopGoodsService cesShopGoodsService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cesShopGoods
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "商品-分页列表查询")
	@ApiOperation(value="商品-分页列表查询", notes="商品-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CesShopGoods cesShopGoods,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CesShopGoods> queryWrapper = QueryGenerator.initQueryWrapper(cesShopGoods, req.getParameterMap());
		Page<CesShopGoods> page = new Page<CesShopGoods>(pageNo, pageSize);
		IPage<CesShopGoods> pageList = cesShopGoodsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cesShopGoods
	 * @return
	 */
	@AutoLog(value = "商品-添加")
	@ApiOperation(value="商品-添加", notes="商品-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CesShopGoods cesShopGoods) {
		cesShopGoodsService.save(cesShopGoods);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cesShopGoods
	 * @return
	 */
	@AutoLog(value = "商品-编辑")
	@ApiOperation(value="商品-编辑", notes="商品-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CesShopGoods cesShopGoods) {
		cesShopGoodsService.updateById(cesShopGoods);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商品-通过id删除")
	@ApiOperation(value="商品-通过id删除", notes="商品-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		cesShopGoodsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "商品-批量删除")
	@ApiOperation(value="商品-批量删除", notes="商品-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cesShopGoodsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商品-通过id查询")
	@ApiOperation(value="商品-通过id查询", notes="商品-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CesShopGoods cesShopGoods = cesShopGoodsService.getById(id);
		if(cesShopGoods==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cesShopGoods);
	}
	
	/**
	 * 通过ids查询
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "商品-通过ids查询")
	@ApiOperation(value="商品-通过ids查询", notes="商品-通过id查询")
	@GetMapping(value = "/queryByIds")
	public Result<?> queryByIds(@RequestParam(name="ids",required=true) String ids) {
		List<CesShopGoodsVo> listcesShopGoods = cesShopGoodsService.getByIds(ids);
		if(listcesShopGoods.size()==0) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(listcesShopGoods);
	}
	
	/**
	 * 通过ids查询
	 *
	 * @param codes
	 * @return
	 */
	@AutoLog(value = "商品-通过codes查询")
	@ApiOperation(value="商品-通过codes查询", notes="商品-通过codes查询")
	@GetMapping(value = "/queryByCodes")
	public Result<?> queryByCodes(@RequestParam(name="codes",required=true) String codes) {
		List<CesShopGoodsVo> listcesShopGoods = cesShopGoodsService.getByCodes(codes);
		if(listcesShopGoods.size()==0) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(listcesShopGoods);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cesShopGoods
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CesShopGoods cesShopGoods) {
        return super.exportXls(request, cesShopGoods, CesShopGoods.class, "商品");
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
        return super.importExcel(request, response, CesShopGoods.class);
    }

}
