package com.nbcio.modules.im.tio;

import org.tio.core.ChannelContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @since 2019-06-24
 */
public class WsOnlineContext {
    private static Map<String, ChannelContext> map = new ConcurrentHashMap<>();

    public static void bindUser(String userName, ChannelContext channelContext) {
        map.put(String.valueOf(userName), channelContext);
    }

    public static ChannelContext getChannelContextByUser(String userName){
        return map.get(userName);
    }
}
