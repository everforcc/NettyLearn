/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-08 10:11
 * Copyright
 */

package cn.cc.netty.base.c4pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 5. 测试 channel
 */
@Slf4j
public class Test5EmbeddedChannel {

    public static void main(String[] args) {

        // 入站处理器 1
        ChannelInboundHandlerAdapter in1 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.info("入站关心读数据: in1");
                ByteBuf byteBuf = (ByteBuf) msg;
                String str = byteBuf.toString(Charset.defaultCharset());
                // 内部的方法，调用下一个方法，将数据传递给下一个 handler
                super.channelRead(ctx, str);
            }
        };

        ChannelInboundHandlerAdapter in2 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.info("入站关心读数据: in2");
                // 内部的方法，调用下一个方法，将数据传递给下一个 handler
                super.channelRead(ctx, msg);
            }
        };

        // 出站处理器 2
        ChannelOutboundHandlerAdapter out1 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.debug("出站: out1");
                super.write(ctx, msg, promise);
            }
        };

        ChannelOutboundHandlerAdapter out2 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.debug("出站: out2");
                super.write(ctx, msg, promise);
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(in1, in2, out1, out2);

        // 模拟入栈操作
        embeddedChannel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("hello".getBytes(StandardCharsets.UTF_8)));

        // 模拟出栈操作
        embeddedChannel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("world".getBytes(StandardCharsets.UTF_8)));
    }

}
