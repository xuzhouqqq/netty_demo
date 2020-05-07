package com.netty.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartBoxChannelMatcher implements ChannelMatcher {
    private static Logger logger = LoggerFactory.getLogger(SmartBoxChannelMatcher.class);
    private String deviceNo;

    public SmartBoxChannelMatcher(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    @Override
    public boolean matches(Channel channel) {
        String hcid = channel.attr(AttributeKey.valueOf(channel.id().asShortText())).get().toString();
        if (hcid.equals(deviceNo)) {
            logger.debug(deviceNo + "   matches ture");
            return true;
        }
        return false;
    }

}