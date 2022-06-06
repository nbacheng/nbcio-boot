package com.nbcio.modules.estar.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * 项目中的常量定义类
 */

public class Constant {
    /**
     * 企业corpid, 需要修改成开发者所在企业
     */
    public static final String CORP_ID = "dingbfdb6ebea66da2c9";
    /**
     * 应用的AppKey，登录开发者后台，点击应用管理，进入应用详情可见
     */
    //public static final String APPKEY = "dingrclmpcbhnquxtpsi";
    /**
     * 应用的AppSecret，登录开发者后台，点击应用管理，进入应用详情可见
     */
    //public static final String APPSECRET = "UT9T-zTD82wqMZc_IJl5qoSUaijoy1_YvN1MKU_7a-_cf9SEbB9erZpZ8wq9HKrl";

    /**
     * 数据加密密钥。用于回调数据的加密，长度固定为43个字符，从a-z, A-Z, 0-9共62个字符中选取,您可以随机生成
     */
    public static final String ENCODING_AES_KEY = "nbcioestar20220128";

    /**
     * 加解密需要用到的token，企业可以随机填写。如 "12345"
     */
    public static final String TOKEN = "nbcioestar123456";

    /**
     * 应用的agentdId，登录开发者后台可查看
     */
    //public static final Long AGENTID = 1105646298L;

    /**
     * 审批模板唯一标识，可以在审批管理后台找到  工资审批，通过postman api请求或通过官方的Api Explorer获取
     */
    public static final String PROCESS_CODE = "PROC-A766DE8A-68A5-4D8C-ABB1-32C719ADB0C4";

    /**
     * 回调host
     */
    public static final String CALLBACK_URL_HOST = "127.0.0.1";
}
