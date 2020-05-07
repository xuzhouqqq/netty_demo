package com.netty.server.netty;

import com.netty.server.channel.SmartBoxChannelHanlder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;


/**
 * @author Administrator
 */
public class SmartBoxNettyServer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(SmartBoxNettyServer.class);
    private static Channel serverChannel;

    public static void stopNettyServer() {
        if (serverChannel != null) {
            logger.info("close server");
            serverChannel.close();
            serverChannel = null;
        }
    }

    @Override
    public void run() {
        logger.info(" -------  smartBox   netty   ------Strart-----");
        //客户端连接(接收)
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //网络读写事件(处理)
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            // 证书
            InputStream certificate = this.getClass().getResourceAsStream("/server.crt");
            // 私钥
            InputStream privateKey = this.getClass().getResourceAsStream("/server.key");
            final SslContext sslContext = SslContextBuilder.forServer(certificate, privateKey).build();
            //配置启动类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 1024);
            //b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // tls
                    // SslHandler要放在最前面
                    SslHandler sslHandler = sslContext.newHandler(ch.alloc());
                    ch.pipeline().addLast(sslHandler);
                    byte[] delimiter = {(byte) 0x7F};
                    ByteBuf buf = Unpooled.copiedBuffer(delimiter);
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(40960, false, buf));
                    //ch.pipeline().addLast(new ReadTimeoutHandler(25, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new IdleStateHandler(10 * 60, 0, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new SmartBoxChannelHanlder());
                }
            });
            //绑定端口
            ChannelFuture channelFuture = b.bind(9993).sync();
            // 将ServerChannel保存下来
            serverChannel = channelFuture.channel();
            //等待服务器退出
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
