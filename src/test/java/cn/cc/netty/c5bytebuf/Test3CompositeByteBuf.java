/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-08 21:33
 * Copyright
 */

package cn.cc.netty.c5bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

/**
 * 3. 把小的 bytebuf 合并为大的 bytebuf
 */
public class Test3CompositeByteBuf {

    public static void main(String[] args) {

        ByteBuf byteBuf1 = ByteBufAllocator.DEFAULT.buffer();
        byteBuf1.writeBytes(new byte[]{1, 2, 3, 4, 5});

        ByteBuf byteBuf2 = ByteBufAllocator.DEFAULT.buffer();
        byteBuf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        // 传统的方式，因为是真正的数据复制,虽然结果正确
//        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
//        byteBuf.writeBytes(byteBuf1).writeBytes(byteBuf2);
//        TestByteBufUtil.log(byteBuf);

        //
        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        // 单个和多个
        //compositeByteBuf.addComponent()
        // 自动增长写指针
        compositeByteBuf.addComponents(true, byteBuf1, byteBuf2);
        TestByteBufUtil.log(compositeByteBuf);
    }

}
