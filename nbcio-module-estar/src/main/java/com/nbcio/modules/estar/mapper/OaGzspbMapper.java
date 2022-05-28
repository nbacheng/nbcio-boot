package com.nbcio.modules.estar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.estar.entity.OaGzspb;

/**
 * @Description: oa_gzspb
 * @Author: nbacheng
 * @Date:   2022-02-14
 * @Version: V1.0
 */

public interface OaGzspbMapper extends BaseMapper<OaGzspb> {
	public List<OaGzspb> getSalaryByDep(@Param("salaryyear") String salaryyear, @Param("salarymonth") String salarymonth, @Param("depno") String depno);
}
