/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-29 16:43
 * Copyright
 */

package cn.cc.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 1. netty 初体验
 * 服务端
 */
public class HelloServer {

    public static void main(String[] args) {
        // 1. 服务器端的启动器，负责组装 netty 组件，启动服务器
        new ServerBootstrap()
                // loop 循环
                // 2. WorkerEventLoop(selector，线程 thread)， group 组
                .group(new NioEventLoopGroup())
                // 3. 选择一个 ServerChannel 实现
                .channel(NioServerSocketChannel.class) // oio bio
                // 4. boss 负责处理连接 worker(child) 处理读写的，决定了 worker(child) 将来能执行哪些操作
                .childHandler(
                        // 5. Channel 数据通道，跟客户端连接后，数据读写的通道
                        // Initializer 初始化器，负责添加别的 handler
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                // 6. 添加具体的 handler
                                // ByteBuf 转为字符串
                                nioSocketChannel.pipeline().addLast(new StringDecoder());
                                // 自定义的 Handler 在读事件发生后怎么处理
                                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        // 打印上一步转换好的字符串
                                        System.out.println(msg);
                                    }
                                });
                            }
                        })
                // 7. 绑定监听端口
                .bind(8080);
    }

}
