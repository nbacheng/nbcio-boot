package org.jeecg.modules.system.estar;

import static com.nbcio.modules.estar.config.URLConstant.URL_GET_TOKKEN;

import org.jeecg.config.thirdapp.ThirdAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiProcessinstanceGetRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.nbcio.modules.estar.apithird.service.IEstarThirdService;
import com.nbcio.modules.estar.config.URLConstant;
import com.taobao.api.ApiException;

/**
 * @Description: 钉钉接口实现表
 * @Author: nbacheng
 * @Date:   2022-06-06
 * @Version: V1.0
 */

@Service
public class EstarThirdServiceImple implements IEstarThirdService {

	private static final Logger bizLogger = LoggerFactory.getLogger(EstarThirdServiceImple.class);

	@Autowired
    ThirdAppConfig thirdAppConfig;
	
	//获取access_token
	@Override
	public String getToken() throws RuntimeException {
		try {
        	String appKey = thirdAppConfig.getDingtalk().getClientId();
    		String appSecret = thirdAppConfig.getDingtalk().getClientSecret();
            DefaultDingTalkClient client = new DefaultDingTalkClient(URL_GET_TOKKEN);
            OapiGettokenRequest request = new OapiGettokenRequest();

            request.setAppkey(appKey);
            request.setAppsecret(appSecret);
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            String accessToken = response.getAccessToken();
            return accessToken;
        } catch (ApiException e) {
            bizLogger.error("getAccessToken failed", e);
            throw new RuntimeException();
        }
	}

	//发送消息
	@Override
	public void sendMessageToOriginator(String processInstanceId) throws RuntimeException {
		try {
            DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_PROCESSINSTANCE_GET);
            OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
            request.setProcessInstanceId(processInstanceId);
            OapiProcessinstanceGetResponse response = client.execute(request, getToken());
            String recieverUserId = response.getProcessInstance().getOriginatorUserid();

            client = new DefaultDingTalkClient(URLConstant.MESSAGE_ASYNCSEND);

            OapiMessageCorpconversationAsyncsendV2Request messageRequest = new OapiMessageCorpconversationAsyncsendV2Request();
            messageRequest.setUseridList(recieverUserId);
            String agentId = thirdAppConfig.getDingtalk().getAgentId();
            messageRequest.setAgentId(Long.parseLong(agentId));
            messageRequest.setToAllUser(false);

            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            msg.setMsgtype("text");
            msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
            msg.getText().setContent("出差申请通过了，快去订机票吧");
            messageRequest.setMsg(msg);

            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(messageRequest,getToken());
        } catch (ApiException e) {
            bizLogger.error("send message failed", e);
            throw new RuntimeException();
        }
	}	

}
