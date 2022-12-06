/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-29 16:59
 * Copyright
 */

package cn.cc.netty.c2eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * 7. 关闭 所有线程
 * NioEventLoopGroup
 */
@Slf4j
public class Test6NioEventLoopGroup {

    public static void main(String[] args) {
        try {
            // 这个也需要关闭
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

            ChannelFuture channelFuture = new Bootstrap()
                    .group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override //[12] 在连接建立后被调用
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            // 使用给定的日志
                            // todo 分析下源码
                            nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            nioSocketChannel.pipeline().addLast(new StringEncoder());
                        }
                    })
                    .connect(new InetSocketAddress("localhost", 8080));

            channelFuture.sync();
            Channel channel = channelFuture.channel();

            log.debug("channel: {}", channel);

            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String line = scanner.nextLine();
                    if ("q".equals(line)) {
                        // close 异步操作,交给其他线程操作
                        channel.close();
                        //log.debug("处理关闭后的操作");
                        break;
                    }
                    channel.writeAndFlush(line);
                }
            }, "input").start();

            //log.debug("处理关闭后的操作");
            ChannelFuture closeFuture = channel.closeFuture();
            log.debug("waiting close...");

            // 两种方式
            // 1. 同步处理关闭
//            closeFuture.sync();
//            log.debug("处理关闭后的操作");

            // 2. 异步处理关闭
            // 执行完关闭了,
            // 优化为 lambda 表达式
            closeFuture.addListener((ChannelFutureListener) future -> {
                log.debug("处理关闭后的操作");
                // 优雅的停下来
                nioEventLoopGroup.shutdownGracefully();
                log.debug("优雅的退出");
            });

        } catch (Exception e) {
            e.printStackTrace();
            // 如果异常了，资源也有都释放掉
        }

    }

}
