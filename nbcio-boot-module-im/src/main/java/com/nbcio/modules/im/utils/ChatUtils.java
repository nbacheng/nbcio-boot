package com.nbcio.modules.im.utils;

import javax.servlet.http.HttpServletRequest;

/**
 *  封装的信息类型 UTILS
 *
 * @author nbacheng
 * @since 2018-10-07
 */
public class ChatUtils {

    /**
     * 单聊
     */
    public static final String MESSAGE_TYPE_FRIEND = "0";
    /**
     * 已读
     */
    public static final String READ_TYPE_READ = "0";

    /**
     * 未读
     */
    public static final String READ_TYPE_UNREAD = "1";

    /**
     * 心跳
     */
    public static final String MSG_PING = "0";

    /**
     * 链接就绪
     */
    public static final String MSG_READY = "1";

    /**
     * 消息
     */
    public static final String MSG_MESSAGE = "2";
    
    /**
     * 消息已读回执
     */
    public static final String MSG_READ = "3";


    public static String getHost(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
    }
}
