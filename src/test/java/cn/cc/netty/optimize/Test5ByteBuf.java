package cn.cc.netty.optimize;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 5. 测试
 * ALLOCATOR
 * RCVBUF_ALLOCATOR
 */
@Slf4j
public class Test5ByteBuf {
    public static void main(String[] args) {

        // 不池化
        System.setProperty("io.netty.allocator.type", "unpooled");
        // 不首选直接内存，就是堆内存
        System.setProperty("io.netty.noPreferDirect", "true");
        // UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf
        // 7. io 直接内存io效率高，强制使用 Direct
        // UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf

        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 6.  ALLOCATOR
                                // UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf
                                //ByteBuf buf = ctx.alloc().buffer();
                                //log.debug("alloc buf {}", buf);

                                // 7. RCVBUF_ALLOCATOR
                                // UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf
                                // 最开始写上的原始数据
                                // handler 是消费者
                                log.debug("receive buf {}", msg);
                                System.out.println("");
                            }
                        });
                    }
                }).bind(8080);
    }
}
