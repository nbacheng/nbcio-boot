/**
 *
 */
package com.nbcio.modules.im.tio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;
import org.tio.core.stat.IpStat;
import org.tio.utils.json.Json;

/**
 * IP 监控
 *
 * @author nbacheng
 * @since 2018-10-08
 */
public class IpStatListener implements org.tio.core.stat.IpStatListener {

    private static Logger log = LoggerFactory.getLogger(IpStatListener.class);

    public static IpStatListener me = new IpStatListener();

    /**
     *
     */
    private IpStatListener() {
    }

    @Override
    public void onExpired(TioConfig tioConfig, IpStat ipStat) {
        if (log.isInfoEnabled()) {
            log.debug("可以把统计数据入库\r\n{}", Json.toFormatedJson(ipStat));
        }
    }

    @Override
    public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect, IpStat ipStat) throws Exception {
        if (log.isInfoEnabled()) {
            log.debug("onAfterConnected\r\n{}", Json.toFormatedJson(ipStat));
        }
    }

    @Override
    public void onDecodeError(ChannelContext channelContext, IpStat ipStat) {
        if (log.isInfoEnabled()) {
            log.debug("onDecodeError\r\n{}", Json.toFormatedJson(ipStat));
        }
    }

    @Override
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess, IpStat ipStat) throws Exception {
        if (log.isInfoEnabled()) {
            log.debug("onAfterSent\r\n{}\r\n{}", packet.logstr(), Json.toFormatedJson(ipStat));
        }
    }

    @Override
    public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize, IpStat ipStat) throws Exception {
        if (log.isInfoEnabled()) {
            log.debug("onAfterDecoded\r\n{}\r\n{}", packet.logstr(), Json.toFormatedJson(ipStat));
        }
    }

    @Override
    public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes, IpStat ipStat) throws Exception {
        if (log.isInfoEnabled()) {
            log.debug("onAfterReceivedBytes\r\n{}", Json.toFormatedJson(ipStat));
        }
    }

    @Override
    public void onAfterHandled(ChannelContext channelContext, Packet packet, IpStat ipStat, long cost) throws Exception {
        if (log.isInfoEnabled()) {
            log.debug("onAfterHandled\r\n{}\r\n{}", packet.logstr(), Json.toFormatedJson(ipStat));
        }
    }

}
