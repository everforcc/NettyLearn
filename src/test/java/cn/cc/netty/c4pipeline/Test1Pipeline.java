/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-07 20:53
 * Copyright
 */

package cn.cc.netty.c4pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 1. 流水管道
 */
@Slf4j
public class Test1Pipeline {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 1. 通过 channel 拿到 pipeline,流水线对象
                        ChannelPipeline channelPipeline = ch.pipeline();
                        // 2. 自动添加处理器
                        // head -> tail
                        // 自己新加的在tail之前，head后面
                        // 最终流程 head -> h1 -> h2 -> 345 -> tail

                        // 3. 入站处理器
                        channelPipeline.addLast("channel-in-bound-h1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("入站关心读数据: h1");
                                super.channelRead(ctx, msg);
                            }
                        });

                        channelPipeline.addLast("channel-in-bound-h2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("入站关心读数据: h2");
                                super.channelRead(ctx, msg);
                            }
                        });

                        channelPipeline.addLast("channel-in-bound-h3", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("入站关心读数据: h3");
                                super.channelRead(ctx, msg);
                                channelPipeline.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes(StandardCharsets.UTF_8)));
                            }
                        });


                        // 4. 出站处理器
                        // 只有向channel写入数据才会触发
                        // 出站是从后往前走 所以输出顺序为 h5 -> h4
                        channelPipeline.addLast("channel-in-bound-h4", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("出站: h4");
                                super.write(ctx, msg, promise);
                            }
                        });

                        channelPipeline.addLast("channel-in-bound-h5", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("出站: h5");
                                super.write(ctx, msg, promise);
                            }
                        });

                    }
                }).bind(8080);
    }

}
