/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-14 10:02
 * Copyright
 */

package cn.cc.netty.advance.c1package;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 2. 黏包半包
 * 客户端 短连接处理
 */
@Slf4j
public class Test2HelloWorldClient {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            shortConnect();
            log.info("finish");
        }
    }

    /**
     * 短链接，发完就断开，这样就不会黏包了
     */
    private static void shortConnect() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 流水线添加事件
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) {
                            // 短连接，用完一次就关闭
                            ByteBuf byteBuf = ctx.alloc().buffer(16);
                            byteBuf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17});
                            ctx.writeAndFlush(byteBuf);
                            ctx.channel().close();
                        }
                    });
                }
            });

            // 链接服务器
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }

}
