package com.nbcio.modules.estar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbcio.modules.estar.entity.OaSalaryapproval;
import com.nbcio.modules.estar.vo.ProcessUpdateVo;

/**
 * @Description: 薪资审批表
 * @Author: nbacheng
 * @Date:   2022-03-03
 * @Version: V1.0
 */
public interface OaSalaryapprovalMapper extends BaseMapper<OaSalaryapproval> {
	public void  updateProcessStatus(@Param("processUpdateVo") ProcessUpdateVo processUpdateVo);
	public OaSalaryapproval getByInstanceId(@Param("processInstanceId") String processInstanceId);
}
