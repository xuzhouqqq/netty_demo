package com.netty.server;


import com.netty.server.netty.SmartBoxNettyServer;
import com.netty.server.task.SendHostCmdThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author Administrator
 * @date 2019/3/27
 */
public class SmartBoxMain {
    private static Logger logger = LoggerFactory.getLogger(SmartBoxMain.class);
    private static ExecutorService consumerExecutor = new ThreadPoolExecutor(10, 15, 5, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(512),
            new ThreadPoolExecutor.DiscardPolicy());

    public static void main(String[] args) {
        try {

            logger.info(" ----SmartBoxMain     netty ---------Strart-----");
            consumerExecutor.execute(new SmartBoxNettyServer());
            SendHostCmdThread sendHostCmdThread = new SendHostCmdThread();
            consumerExecutor.execute(sendHostCmdThread);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    SmartBoxNettyServer.stopNettyServer();
                    sendHostCmdThread.interrupt();
                    countDownLatch.await(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("stop SmartBoxMain success");
            }));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
