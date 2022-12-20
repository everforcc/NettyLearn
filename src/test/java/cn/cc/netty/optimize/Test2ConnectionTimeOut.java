/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-19 16:28
 * Copyright
 */

package cn.cc.netty.optimize;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试序列化
 */
@Slf4j
public class Test2ConnectionTimeOut {
    // 1. 客户端通过 .option() 方法配置参数
    // 2. 服务端

    /**
     * new ServerBootstrap().option() // 给 ServerSocketChannel 配置参数
     * new ServerBootstrap().childOption() // 给 SocketChannel 配置参数
     */


    public static void main(String[] args) {
//        new ServerBootstrap().option() // 给 ServerSocketChannel 配置参数
//        new ServerBootstrap().childOption() // 给 SocketChannel 配置参数
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler());
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080);
            future.sync().channel().closeFuture().sync(); // 断点1
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("timeout");
        } finally {
            group.shutdownGracefully();
        }
    }

}
