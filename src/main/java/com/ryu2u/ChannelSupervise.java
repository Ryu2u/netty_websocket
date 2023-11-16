package com.ryu2u;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 连接管理
 *
 * @author Administrator
 * @Description:
 * @date 2023/5/9 13:51
 */
public class ChannelSupervise {
    private static final ChannelGroup GLOBAL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final ConcurrentHashMap<String, ChannelGroup> GROUP_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ChannelId> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 添加到总的group组中
     * 之后可以进行广播通知
     *
     * @param channel
     */
    public static void addChannel(Channel channel) {
        GLOBAL_GROUP.add(channel);
    }

    /**
     * 使用userid 作为map 的key保存
     *
     * @param userid
     * @param channel
     * @return 是否插入map成功, true表示之前channel已断开，并更新了channel,false 表示channel未断开,null 表示连接不存在
     */
    public static Boolean putChannelId(Channel channel, String userid) {
        if (userid == null || "".equals(userid)) {
            return null;
        }
        ChannelId channelId = CHANNEL_MAP.get(userid);
        if (channelId == null || channelId != channel.id()) {
            CHANNEL_MAP.put(userid, channel.id());
            return true;
        }
        return false;
    }

    public static void removeChannel(Channel channel) {
        GLOBAL_GROUP.remove(channel);
    }

    public static Channel findChannel(String id) {
        ChannelId channelId = CHANNEL_MAP.get(id);
        if (channelId == null) {
            return null;
        }
        return GLOBAL_GROUP.find(channelId);
    }

    public static void send2All(WebSocketFrame tws) {
        GLOBAL_GROUP.writeAndFlush(tws);
    }

}
