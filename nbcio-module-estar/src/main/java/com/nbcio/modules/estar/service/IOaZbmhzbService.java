package com.nbcio.modules.estar.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.estar.entity.OaZbmhzb;

/**
 * @Description: oa_zbmhzb
 * @Author: nbacheng
 * @Date:   2022-02-24
 * @Version: V1.0
 */
public interface IOaZbmhzbService extends IService<OaZbmhzb> {
	public List<OaZbmhzb> getTotalSalaryBySubDep(String salarytime, String bmbh);
}
