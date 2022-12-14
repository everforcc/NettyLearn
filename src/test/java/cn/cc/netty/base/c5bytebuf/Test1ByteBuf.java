/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-07 20:53
 * Copyright
 */

package cn.cc.netty.base.c5bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

/**
 * 1. ByteBuf
 */
@Slf4j
public class Test1ByteBuf {

    public static void main(String[] args) {
        // todo 初始容量 256
        //
        // -Dio.netty.allocator.type={unpooled|pooled}
        System.setProperty("io.netty.allocator.type", "unpooled");


        // 1. 创建,可以动态扩容
        // PooledUnsafeDirectByteBuf
        // UnpooledByteBufAllocator $InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        // PooledUnsafeHeapByteBuf
        // UnpooledByteBufAllocator $InstrumentedUnpooledUnsafeHeapByteBuf
        //ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        log.info("byteBuf.getClass(): {}", byteBuf.getClass());
        TestByteBufUtil.log(byteBuf);
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < 32; i++) {
            stringBuilder.append("a");
        }
        byteBuf.writeBytes(stringBuilder.toString().getBytes());
        TestByteBufUtil.log(byteBuf);
    }

}
