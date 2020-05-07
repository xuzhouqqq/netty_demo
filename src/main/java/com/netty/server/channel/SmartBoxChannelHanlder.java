package com.netty.server.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * @date 2018/3/27
 */
public class SmartBoxChannelHanlder extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(SmartBoxChannelHanlder.class);

    /**
     * 设备远程地址
     */
    private String remoteAddress;
    /**
     * 登陆
     */
    public byte[] rep_login = {
            (byte) 0x02, (byte) 0x00, //帧控制，包序号
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,//源地址
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,//目的地
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, //时间戳
            (byte) 0x00, (byte) 0x03,//长度
            (byte) 0x00,//主命令
            (byte) 0x00,//子命令
            (byte) 0x01//成功
    };

    /**
     * 心跳
     */
    public byte[] rep_heart = {
            (byte) 0x02, (byte) 0x00, //帧控制，包序号
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,//源地址
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,//目的地
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, //时间戳
            (byte) 0x00, (byte) 0x03,//长度
            (byte) 0x00,//主命令
            (byte) 0x01,//子命令
            (byte) 0x01//成功
    };

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] response = null;
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(response));
        }
        ReferenceCountUtil.release(byteBuf);
    }


    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        //todo  启停记录
        remoteAddress = ctx.channel().remoteAddress().toString().replace("/", "");
        logger.info("create connection " + remoteAddress);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //todo  启停记录
        //从分组去掉连接
        ChannelHanlderGroup.getChannelGroup(1).remove(ctx.channel().id());
        logger.info("disconnect connection :" + ctx.channel().remoteAddress());
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ChannelHanlderGroup.getChannelGroup(1).remove(ctx.channel().id());
        ctx.close();
        cause.printStackTrace();
        logger.error(cause.getMessage(), cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
