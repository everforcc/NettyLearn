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
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 5. ChannelFuture 测试
 */
@Slf4j
public class Test4ChannelFuture {

    // todo Future/Promise 一般都是异步相关,用来处理结果
    public static void main(String[] args) {
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override //[12] 在连接建立后被调用
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(new StringEncoder());
                        }
                    })
                    // 异步的非阻塞，调用的不在乎结果
                    // main线程发起了调用，真正执行 connect 连接的是另一个线程 NioEventLoopGroup 中的
                    // 可能要1s才能建立连接
                    .connect(new InetSocketAddress("localhost", 8080));

//             处理方式 一
//             同步
//             阻塞线程，直到连接建立
//            channelFuture.sync();
//             如果没有sync，无阻塞，就会瞬间获取channel
//            Channel channel = channelFuture.channel();
//            // 如果已经正确连接，会打印服务器的连接端口等
//            log.debug("{}", channel);
//            channel.writeAndFlush("hello world");

            // 处理方式 二
            // 异步
            // 使用 addListener 方法异步处理结果，回调对象
            channelFuture.addListener(new ChannelFutureListener() {
                // operation 在 nio 线程连接建立好之后，会调用这个方法
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Channel channel = future.channel();
                    log.debug("{}", channel);
                    channel.writeAndFlush("nio 线程调用发送数据: hello world");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
