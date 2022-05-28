package com.nbcio.modules.estar.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbcio.modules.estar.entity.OaGzspb;
import com.nbcio.modules.estar.util.ServiceResult;

/**
 * @Description: oa_gzspb
 * @Author: nbacheng
 * @Date:   2022-02-14
 * @Version: V1.0
 */
public interface IOaGzspbService extends IService<OaGzspb> {
	public List<OaGzspb> getSalaryByDep(String salaryyear, String salarymonth, String depno);
	public ServiceResult<String> SalaryApprove(String salaryyear, String salarymonth, String depno);
}
