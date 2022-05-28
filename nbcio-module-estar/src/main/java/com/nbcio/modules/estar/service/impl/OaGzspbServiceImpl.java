package com.nbcio.modules.estar.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import com.nbcio.modules.estar.config.Constant;
import com.nbcio.modules.estar.config.URLConstant;
import com.nbcio.modules.estar.controller.OaGzspbController;
import com.nbcio.modules.estar.entity.OaGzspb;
import com.nbcio.modules.estar.entity.OaSubdep;
import com.nbcio.modules.estar.entity.OaZbmhzb;
import com.nbcio.modules.estar.mapper.OaGzspbMapper;
import com.nbcio.modules.estar.model.ProcessInstanceInputVO;
import com.nbcio.modules.estar.service.IOaGzspbService;
import com.nbcio.modules.estar.service.IOaSubdepService;
import com.nbcio.modules.estar.service.IOaZbmhzbService;
import com.nbcio.modules.estar.util.AccessTokenUtil;
import com.nbcio.modules.estar.util.LogFormatter;
import com.nbcio.modules.estar.util.ServiceResult;
import com.nbcio.modules.estar.util.ServiceResultCode;
import com.nbcio.modules.estar.util.LogFormatter.LogEvent;
import com.nbcio.modules.flowable.service.impl.FlowDefinitionServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: oa_gzspb
 * @Author: nbacheng
 * @Date:   2022-02-14
 * @Version: V1.0
 */
@Service
@Slf4j
public class OaGzspbServiceImpl extends ServiceImpl<OaGzspbMapper, OaGzspb> implements IOaGzspbService {
	@Autowired
	  OaGzspbMapper oaGzspbMapper;
	@Autowired
	private IOaGzspbService  oaGzspbService;
	@Autowired
	private IOaSubdepService oaSubdepService;
	@Autowired
	private IOaZbmhzbService oaZbmhzbService;
	@Autowired	
	FlowDefinitionServiceImpl flowDefinitionServiceImpl;
	
	@Override
	public List<OaGzspb> getSalaryByDep(String salaryyear, String salarymonth, String depno) 
	{
		List<OaGzspb> oaGzspbList = oaGzspbMapper.getSalaryByDep(salaryyear, salarymonth, depno);
		return oaGzspbList;
		
	}
	
	@Override
	public ServiceResult<String> SalaryApprove(String salaryyear, String salarymonth, String depno) {
		ProcessInstanceInputVO processInstance = new ProcessInstanceInputVO();
		try {
			List<OaGzspb> oaGzspbList = oaGzspbService.getSalaryByDep(salaryyear, salarymonth, depno);
			List<OaSubdep> oaDepList = oaSubdepService.getDep(depno);			
			if (oaDepList.size() > 0 ) {
				processInstance.setApprovers("010155213221415221");
				processInstance.setOriginatorUserId("010155213221415221");
				processInstance.setCcList("010155213221415221");
				processInstance.setDeptId(593680465L);
				DefaultDingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_PROCESSINSTANCE_START);
				OapiProcessinstanceCreateRequest request = new OapiProcessinstanceCreateRequest();
				request.setProcessCode(Constant.PROCESS_CODE);
				request.setApprovers(processInstance.getOriginatorUserId());
				request.setOriginatorUserId(processInstance.getOriginatorUserId());
				request.setDeptId(processInstance.getDeptId());
				request.setCcList(processInstance.getOriginatorUserId());
				//request.setCcPosition("START_FINISH");
				request.setCcPosition("START");

				JSONArray jsonlist = new JSONArray();
				List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValueVoList = new ArrayList<OapiProcessinstanceCreateRequest.FormComponentValueVo>();
				
				// 部门名称的单行显示
				
				OapiProcessinstanceCreateRequest.FormComponentValueVo fmbm = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
				formComponentValueVoList.add(fmbm);
				fmbm.setName("部门");
				fmbm.setValue(oaDepList.get(0).getDepname());

				// 年月的单行显示
				OapiProcessinstanceCreateRequest.FormComponentValueVo fmyearmonth = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
				formComponentValueVoList.add(fmyearmonth);
				fmyearmonth.setName("年月");
				fmyearmonth.setValue(salaryyear + "年" + salarymonth + "月");
				
				if (oaGzspbList.size() > 0 ) {
	            	// 本部门薪资汇总表明细  拼凑明细表的json数据，否则会失败
	    			OapiProcessinstanceCreateRequest.FormComponentValueVo fmtotalsalary = new OapiProcessinstanceCreateRequest.FormComponentValueVo();

	            	// 总额的单行显示
	    			OapiProcessinstanceCreateRequest.FormComponentValueVo fmtotal = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
	    			formComponentValueVoList.add(fmtotal);
	    			fmtotal.setName("总额");
	    			BigDecimal total = new BigDecimal(0);
	    			BigDecimal totaldkbx = new BigDecimal(0.00);
	    			for (int i = 0; i < oaGzspbList.size(); i++) {
	    				total = total.add(oaGzspbList.get(i).getYdjj());
	    				totaldkbx = totaldkbx.add(oaGzspbList.get(i).getDkbx());
	    				
	    			}
	    			fmtotal.setValue(total.toString());
	            	for (int i = 0; i < oaGzspbList.size(); i++) { //i < oaGzspbList.size()
						OapiProcessinstanceCreateRequest.FormComponentValueVo itempno = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itusername = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itdepno = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itdepname = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itydjj = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itdkbx = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itregisterdate = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						itempno.setName("工号");
						itempno.setValue(oaGzspbList.get(i).getEmpno());
						itusername.setName("姓名");
						itusername.setValue(oaGzspbList.get(i).getUsername());
						itdepno.setName("部门编号");
						itdepno.setValue(oaGzspbList.get(i).getDepno());
						itdepname.setName("部门");
						itdepname.setValue(oaGzspbList.get(i).getDepname());
						itydjj.setName("个人奖金");
						itydjj.setValue(oaGzspbList.get(i).getYdjj().toString());
						itdkbx.setName("代扣保险");
						itdkbx.setValue(oaGzspbList.get(i).getDkbx().toString());
						itregisterdate.setName("入司时间");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String regdate = sdf.format(oaGzspbList.get(i).getRegisterdate());
						itregisterdate.setValue(regdate);
						jsonlist.add(Arrays.asList(itempno, itusername, itdepno, itdepname, itydjj, itdkbx, itregisterdate));
					} 
	            	//添加需要的合计项目
	            	OapiProcessinstanceCreateRequest.FormComponentValueVo itempno = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itusername = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itdepno = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itdepname = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itydjj = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itdkbx = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itregisterdate = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
	            	itempno.setName("工号");
					itempno.setValue("合计");
					itusername.setName("姓名");
					itusername.setValue("");
					itdepno.setName("部门编号");
					itdepno.setValue("");
					itdepname.setName("部门");
					itdepname.setValue("");
					itydjj.setName("个人奖金");
					itydjj.setValue(total.toString());
					itdkbx.setName("代扣保险");
					itdkbx.setValue(totaldkbx.toString());
					jsonlist.add(Arrays.asList(itempno, itusername, itdepno, itdepname, itydjj, itdkbx));//日期就不添加
					fmtotalsalary.setName("本部门工资汇总表");
					//log.info(jsonlist.toJSONString());
					fmtotalsalary.setValue(jsonlist.toJSONString());
					formComponentValueVoList.add(fmtotalsalary);
		        }
				//有子部门的话显示子部门汇总数据
				//部门，人数 ，实发工资，加班费，实发奖金，上月实发工资 ，上月加班费 ，上月实发奖金
				// 子部门薪资汇总汇表  拼凑明细表的json数据，否则会失败
				OapiProcessinstanceCreateRequest.FormComponentValueVo fmsubsalary = new OapiProcessinstanceCreateRequest.FormComponentValueVo();

				String salarytime = salaryyear.toString() + salarymonth.toString();
				List<OaSubdep> oaSubdepList = oaSubdepService.getSubDep(depno);
				jsonlist.clear();
				
				Integer totalrs = new Integer(0);
				BigDecimal totalsfgz = new BigDecimal(0.00);
				if (oaSubdepList.size() > 0 ) {
					for (int i = 0; i < oaSubdepList.size(); i++) {
						List<OaZbmhzb> oaZbmhzbList = oaZbmhzbService.getTotalSalaryBySubDep(salarytime, oaSubdepList.get(i).getDepno());
						OapiProcessinstanceCreateRequest.FormComponentValueVo itdepname = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itrs = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itsfgz = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itjbf = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itsfjj = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itsysfgz = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itsyjbf = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						OapiProcessinstanceCreateRequest.FormComponentValueVo itsysfjj = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
						
						itdepname.setName("部门名称");
						itdepname.setValue(oaZbmhzbList.get(0).getDepname());
						itrs.setName("人数");
						itrs.setValue(oaZbmhzbList.get(0).getRs().toString());
						itsfgz.setName("实发工资");
						itsfgz.setValue(oaZbmhzbList.get(0).getBysfgz().toString());
						itjbf.setName("加班费");
						itjbf.setValue(oaZbmhzbList.get(0).getByjbf().toString());
						itsfjj.setName("实发奖金");
						itsfjj.setValue(oaZbmhzbList.get(0).getBysfjj().toString());
						itsysfgz.setName("上月实发工资");
						itsysfgz.setValue(oaZbmhzbList.get(0).getSysfgz().toString());
						itsyjbf.setName("上月加班费");
						itsyjbf.setValue(oaZbmhzbList.get(0).getSyjbf().toString());
						itsysfjj.setName("上月实发奖金");
						itsysfjj.setValue(oaZbmhzbList.get(0).getSysfjj().toString());
						
						jsonlist.add(Arrays.asList(itdepname,itrs,itsfgz,itjbf,itsfjj,itsysfgz,itsyjbf,itsysfjj));
						totalsfgz = totalsfgz.add(oaZbmhzbList.get(0).getBysfgz());
						totalrs += oaZbmhzbList.get(0).getRs();
			
					}
					
					//添加需要的合计项目
					OapiProcessinstanceCreateRequest.FormComponentValueVo itdepname = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itrs = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itsfgz = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itjbf = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itsfjj = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itsysfgz = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itsyjbf = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					OapiProcessinstanceCreateRequest.FormComponentValueVo itsysfjj = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
					
					itdepname.setName("部门名称");
					itdepname.setValue("合计");
					itrs.setName("人数");
					itrs.setValue(totalrs.toString());
					itsfgz.setName("实发工资");
					itsfgz.setValue(totalsfgz.toString());
					/*itjbf.setName("加班费");
					itjbf.setValue(oaZbmhzbList.get(0).getByjbf().toString());
					itsfjj.setName("实发奖金");
					itsfjj.setValue(oaZbmhzbList.get(0).getBysfjj().toString());
					itsysfgz.setName("上月实发工资");
					itsysfgz.setValue(oaZbmhzbList.get(0).getSysfgz().toString());
					itsyjbf.setName("上月加班费");
					itsyjbf.setValue(oaZbmhzbList.get(0).getSyjbf().toString());
					itsysfjj.setName("上月实发奖金");
					itsysfjj.setValue(oaZbmhzbList.get(0).getSysfjj().toString());*/
					
					jsonlist.add(Arrays.asList(itdepname,itrs,itsfgz));
					//jsonlist.add(Arrays.asList(itdepname,itrs,itsfgz,itjbf,itsfjj,itsysfgz,itsyjbf,itsysfjj));
					
					fmsubsalary.setName("下级部门薪资汇总");
					//log.info(jsonlist.toJSONString());
					fmsubsalary.setValue(jsonlist.toJSONString());
					formComponentValueVoList.add(fmsubsalary);
				}
				
				//获取流程审批人员
				JSONArray jsonflow = new JSONArray();
				jsonflow = flowDefinitionServiceImpl.ListAllNode(oaDepList.get(0).getDepname());
				
				//设置审批人，会签、或签设置的审批人必须大于等于2个人  这个最新V2版本才支持，老版本不支持
		        List<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo> processInstanceApproverVoList = new ArrayList<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo>();
		        
                for(int i=0;i<jsonflow.size();i++){
                	OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo processInstanceApproverVo = new OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo();
    		        processInstanceApproverVoList.add(processInstanceApproverVo);
    		        JSONObject jsonObject = (JSONObject) jsonflow.get(i);
    		        processInstanceApproverVo.setUserIds(Arrays.asList(jsonObject.getString("Assignee")));
                }
		       
		        /*
		        OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo processInstanceApproverVo1 = new OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo();
		        processInstanceApproverVoList.add(processInstanceApproverVo1);
		        processInstanceApproverVo1.setUserIds(Arrays.asList("4539296845-893905953"));
		        
		        OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo processInstanceApproverVo2 = new OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo();
		        processInstanceApproverVoList.add(processInstanceApproverVo2);
		        processInstanceApproverVo2.setTaskActionType("AND");
		        processInstanceApproverVo2.setUserIds(Arrays.asList("084517694032399222","151343022129431757"));
		        
		        
		        OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo processInstanceApproverVo3 = new OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo();
		        processInstanceApproverVoList.add(processInstanceApproverVo3);
		        processInstanceApproverVo3.setTaskActionType("OR");
		        processInstanceApproverVo3.setUserIds(Arrays.asList("014316552361903146","010155213221415221"));*/
		        
		        request.setApproversV2(processInstanceApproverVoList);
				
		        request.setFormComponentValues(formComponentValueVoList);
				OapiProcessinstanceCreateResponse response = client.execute(request, AccessTokenUtil.getToken());

				if (response.getErrcode().longValue() != 0) {
					return ServiceResult.failure(String.valueOf(response.getErrorCode()), response.getErrmsg());
				}
				return ServiceResult.success(response.getProcessInstanceId());
				
			}
			else {
				return ServiceResult.failure("没有查到部门！");
			}
				
		} catch (Exception e) {
			String errLog = LogFormatter.getKVLogData(LogEvent.END,
					LogFormatter.KeyValue.getNew("processInstance", JSON.toJSONString(processInstance)));
			log.info(errLog, e);
			return ServiceResult.failure(ServiceResultCode.SYS_ERROR.getErrCode(),
					ServiceResultCode.SYS_ERROR.getErrMsg());
		}
	}
}
