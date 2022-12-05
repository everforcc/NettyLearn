/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-05 15:16
 * Copyright
 */

package cn.cc.netty.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * 3. io事件
 * 细分
 */
@Slf4j
public class Test2EventLoopServer {

    public static void main(String[] args) {

        new ServerBootstrap()
                // 职责划分的更细
                // boss 和 worker
                // todo ctrl+p
                //.group(new NioEventLoopGroup())
                // 参数一 boss accept，参数二 worker 读写操作
                // 不需要设置线程数
                // 第一个只会找到一个 一个 EventLoop绑定，第二个按自己需要设置
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override // msg ByteBuf 类型
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // msg 转换字符串
                                // 同一个 EventLoop 处理请求
                                ByteBuf byteBuf = ((ByteBuf) msg);
                                log.debug(byteBuf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                }).bind(8080);
    }

}
