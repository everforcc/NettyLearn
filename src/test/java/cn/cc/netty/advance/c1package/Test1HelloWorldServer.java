/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-14 09:40
 * Copyright
 */

package cn.cc.netty.advance.c1package;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 1. 黏包半包
 * 服务端
 * 滑动窗口
 */
@Slf4j
public class Test1HelloWorldServer {

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);

            // todo 设置接收缓冲区 so 是 socket的缩写 tcp缓冲区
            // 为了更明确看到黏包半包现象
            // 服务端滑动窗口
            // 全局的
            //serverBootstrap.option(ChannelOption.SO_RCVBUF, 10);
            // 客户端滑动窗口
            //ChannelOption.SO_SNDBUF

            // 调整 netty 的接收缓冲区 ByteBuf
            // 针对每个选项连接的 非全局
            serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16, 16, 16));

            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }

}
