package org.jeecg.modules.system.bird;


import java.util.ArrayList;
import java.util.Iterator;

import com.nbcio.modules.estar.entity.OaSalaryapproval;
import com.nbcio.modules.estar.mapper.OaSalaryapprovalMapper;
import com.nbcio.modules.estar.service.IOaProcessinstanceService;
import com.nbcio.modules.estar.vo.ProcessUpdateVo;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.vo.SysUserNameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse.ProcessInstanceTopVo;





/**
 * bird模块与业务接口实现类
 *@author nbacheng
 *@date 2022/04/02
 *@version 1.0
 */
@Service
public class OaProcessinstanceServiceImpl implements IOaProcessinstanceService {
	
	@Autowired
	ISysUserService sysUserService;
	@Autowired
	OaSalaryapprovalMapper OaSalaryapprovalMapper;
	
	
	@Override
	public ProcessInstanceTopVo getProcesseDetailByUserIds(ProcessInstanceTopVo processInstanceTopVo) {
		JSONObject jsonObject =  (JSONObject) JSONObject.toJSON(processInstanceTopVo); //  .parseObject(processInstanceTopVo.toString());
		
		//替换抄送人员
		JSONArray ccUserids = jsonObject.getJSONArray("ccUserids");
		ArrayList<String> ccUsername = new ArrayList<String>();
		for(int i=0;i<ccUserids.size();i++) {
			ccUsername.add(sysUserService.getRealNameByName(ccUserids.getString(i)).getRealName());
		}
		ccUserids.clear();
		ccUserids.add(ccUsername);
		
		//替换发起人
		String originatorUserid = jsonObject.getString("originatorUserid");
		String originatorUserName = sysUserService.getRealNameByName(originatorUserid).getRealName();
		jsonObject.put("originatorUserid", originatorUserName);
		
		//递归遍历替换json里面的userid值，主要是在task里
		jsonObject = (JSONObject)jsonRecursion(jsonObject,"userid");
		
		
		ProcessInstanceTopVo newprocessInstanceTopVo = JSONObject.toJavaObject(jsonObject, ProcessInstanceTopVo.class);
		return newprocessInstanceTopVo;
	}
 

	 /**
	     * 处理是否需要更新薪资流程表，根据是否完成，是完成就需要更新表
	     * @param ProcessInstanceTopVo 
	 * @return 
	     * @return
	     */
	@Override
	public void UpdateProcessStatus(String instanceId, ProcessInstanceTopVo processInstanceTopVo) {
			JSONObject jsonObject =  (JSONObject) JSONObject.toJSON(processInstanceTopVo);
			OaSalaryapproval oaSalaryapproval = OaSalaryapprovalMapper.getByInstanceId(instanceId);
			if(oaSalaryapproval.getProcessStatus() == 1) {
			  ProcessUpdateVo processUpdateVo = new ProcessUpdateVo();
			  if(jsonObject.getString("status").equals("COMPLETED")) {
				if(jsonObject.getString("result").equals("agree")) {
					processUpdateVo.setProcessStatus(2);
					processUpdateVo.setProcessInstanceId(instanceId);
					processUpdateVo.setEndTime(jsonObject.getDate("finishTime"));	
				}
				else if(jsonObject.getString("result").equals("refuse")) {
					processUpdateVo.setProcessStatus(3);
					processUpdateVo.setProcessInstanceId(instanceId);
					processUpdateVo.setEndTime(jsonObject.getDate("finishTime"));	
				}
				OaSalaryapprovalMapper.updateProcessStatus(processUpdateVo);
		      }
			}
			
	    }
	
	 /**
	     * 通过递归遍历json，替换节点的值
	     * @param oJson JSON数据源
	     * @param key   替换Key的值，这里就替换userid的钉钉用户名为realname
	     * @return
	     */
	    public  Object jsonRecursion(Object oJson, String key) {
	        if (oJson == null || oJson == "") return null;
	        try {
	            if (oJson instanceof JSONObject) {
	                JSONObject jo = (JSONObject) oJson;
	                Iterator<String> iterator = jo.keySet().iterator();
	                while (iterator.hasNext()) {
	                    String keys = iterator.next();
	                    if (key.equals(keys)) {
	                    	
							String keyvalue = jo.getString(key);
							if ( keyvalue != null) {
								SysUserNameVo sysUserName = sysUserService.getRealNameByName(keyvalue);
								String realname = sysUserName.getRealName();
		                        jo.put(key.toString(), realname);
							}
	                    } else {
	                        Object node = jo.get(keys);
	                        if (node instanceof JSONObject) {
	                        	jsonRecursion(node, key);
	                        } else if (node instanceof JSONArray) {
	                            JSONArray innerArr = (JSONArray) node;
	                            jsonRecursion(innerArr, key);
	                        } else {
	                            continue;
	                        }
	                    }
	                }
	            } else if (oJson instanceof JSONArray) {
	                JSONArray ja = (JSONArray) oJson;
	                int size = ja.size();
	                for (int i = 0; i < size; i++) {
	                    Object o = ja.get(i);
	                    if (o != null && o != "") {
	                    	jsonRecursion(o, key);
	                    }
	                }
	            }
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        }
	        return oJson;
	    }
	
}
