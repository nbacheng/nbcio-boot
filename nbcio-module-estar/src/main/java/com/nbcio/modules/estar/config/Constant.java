package com.nbcio.modules.estar.config;

/**
 * 项目中的常量定义类
 */
public class Constant {
    /**
     * 企业corpid, 需要修改成开发者所在企业
     */
    public static final String CORP_ID = "ding46c9a71a4a02223ca39a90f97fcb1e09";
    /**
     * 应用的AppKey，登录开发者后台，点击应用管理，进入应用详情可见
     */
    public static final String APPKEY = "dingaii70403vq3eafof";
    /**
     * 应用的AppSecret，登录开发者后台，点击应用管理，进入应用详情可见
     */
    public static final String APPSECRET = "dr-U5LuqHZ4Ygs2PSsY_DRwYt_D8WFPHvpl5a2fBwLCTO-UN8F1Jey1ZMzbbRzRz";

    /**
     * 数据加密密钥。用于回调数据的加密，长度固定为43个字符，从a-z, A-Z, 0-9共62个字符中选取,您可以随机生成
     */
    public static final String ENCODING_AES_KEY = "birdgf20220128";

    /**
     * 加解密需要用到的token，企业可以随机填写。如 "12345"
     */
    public static final String TOKEN = "birdgf123456";

    /**
     * 应用的agentdId，登录开发者后台可查看
     */
    public static final Long AGENTID = 1449967715L;

    /**
     * 审批模板唯一标识，可以在审批管理后台找到  工资审批，通过postman api请求获取
     */
    public static final String PROCESS_CODE = "PROC-5DAE4DAB-99B7-46CC-990E-8BC07F4AF27E";

    /**
     * 回调host
     */
    public static final String CALLBACK_URL_HOST = "221.136.86.243";
}
