/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-29 16:59
 * Copyright
 */

package cn.cc.netty.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * netty 客户端
 */
public class HelloClient {

    public static void main(String[] args) {
        // 1. 启动器类
        try {
            new Bootstrap()
                    // 2. 添加一些组件 EventLoop
                    .group(new NioEventLoopGroup())
                    // 3. 选择客户端的 channel 实现
                    .channel(NioSocketChannel.class)
                    // 4. 添加处理器
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override // 在连接建立后被调用
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            // 需要一个编码器
                            nioSocketChannel.pipeline().addLast(new StringEncoder());
                        }
                    })
                    // 5. 连接到服务器
                    .connect(new InetSocketAddress("localhost", 8080))
                    .sync()
                    .channel()
                    // 6. 向服务器发送数据
                    .writeAndFlush("hello world");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
