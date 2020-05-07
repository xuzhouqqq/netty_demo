package com.netty.server.task;

import com.netty.server.channel.ChannelHanlderGroup;
import com.netty.server.netty.SmartBoxChannelMatcher;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @projectName: netty_demo
 * @package: com.netty.server.task
 * @description: 发送指令到设备
 * @author: xuzhou
 * @createDate: 2018/2/24 11:40
 * @updateUser: xuzhou
 * @updateDate: 2018/2/24 11:40
 * @updateRemark: The modified content
 * @version: 1.0
 * <p>Copyright: Copyright (c) 2018</p>
 */
public class SendHostCmdThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(SendHostCmdThread.class);

    @Override
    public void run() {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            logger.info("发送指令到设备,...");
            byte[] response = new byte[16];
            String deviceNo = "0101ee2aed01a801";
            ChannelHanlderGroup.getChannelGroup(1).writeAndFlush(Unpooled.copiedBuffer(response), new SmartBoxChannelMatcher(deviceNo));
        }
    }
}
