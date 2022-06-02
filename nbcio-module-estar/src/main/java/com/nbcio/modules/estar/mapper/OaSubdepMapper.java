package com.nbcio.modules.estar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.estar.entity.OaSubdep;

/**
 * @Description: oa_subdep
 * @Author: nbacheng
 * @Date:   2022-02-24
 * @Version: V1.0
 */
public interface OaSubdepMapper extends BaseMapper<OaSubdep> {
	public List<OaSubdep> getSubDep(@Param("depno") String depno);
	public List<OaSubdep> getDep(@Param("depno") String depno);
}
