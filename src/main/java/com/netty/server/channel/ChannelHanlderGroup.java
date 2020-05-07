package com.netty.server.channel;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author Administrator
 * @date 2018/3/27
 */
public class ChannelHanlderGroup {


    private static DefaultChannelGroup[] dcg = new DefaultChannelGroup[16];


    public static DefaultChannelGroup getChannelGroup(int num) {
        if (dcg[num] == null) {
            dcg[num] = new DefaultChannelGroup(String.valueOf(num), GlobalEventExecutor.INSTANCE);
        }
        return dcg[num];
    }
}
