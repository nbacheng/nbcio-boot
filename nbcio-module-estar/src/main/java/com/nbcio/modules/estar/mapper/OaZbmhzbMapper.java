package com.nbcio.modules.estar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.estar.entity.OaZbmhzb;

/**
 * @Description: oa_zbmhzb
 * @Author: nbacheng
 * @Date:   2022-02-24
 * @Version: V1.0
 */

public interface OaZbmhzbMapper extends BaseMapper<OaZbmhzb> {
	//salarytime就是年月的字符串,MybatisPlus版本为3.4.0及以上，多租户屏蔽接口，否则这个sql出现错误
	@InterceptorIgnore(tenantLine = "true")
	public List<OaZbmhzb> getTotalSalaryBySubDep(@Param("salarytime") String salarytime, @Param("bmbh") String bmbh);
	
}
