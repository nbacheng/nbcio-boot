/**
 *
 */
package com.nbcio.modules.im.tio;

import org.tio.utils.time.Time;

/**
 * TIO 配置文件
 *
 * @author nbacheng
 * @since 2018-04-10
 */
public abstract class TioServerConfig {
    /**
     * 协议名字(可以随便取，主要用于开发人员辨识)
     */
    public static final String PROTOCOL_NAME = "V-IM";

    public static final String CHARSET = "utf-8";


    /**
     * 监听端口
     */
    public static final int SERVER_PORT = 9326;

    /**
     * 心跳超时时间，单位：毫秒
     */
    public static final int HEARTBEAT_TIMEOUT = 1000 * 60;

    /**
     * ip数据监控统计，时间段
     *
     * @author tanyaowu
     */
    public interface IpStatDuration {
        Long DURATION_1 = Time.MINUTE_1 * 5;
        Long[] IPSTAT_DURATIONS = new Long[]{DURATION_1};
    }

}
