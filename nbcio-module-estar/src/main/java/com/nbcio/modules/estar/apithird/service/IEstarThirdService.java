package com.nbcio.modules.estar.apithird.service;

/**
 * @Description: 钉钉接口类
 * @Author: nbacheng
 * @Date:   2022-06-06
 * @Version: V1.0
 */
public interface IEstarThirdService {
	String getToken();
	void sendMessageToOriginator(String processInstanceId);
}
