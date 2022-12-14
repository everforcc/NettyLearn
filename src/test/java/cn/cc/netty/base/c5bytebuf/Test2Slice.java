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
 * 2. 测试 netty 零拷贝
 */
@Slf4j
public class Test2Slice {

    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(10);
        byteBuf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        TestByteBufUtil.log(byteBuf);

        // 切片过程中，并没有发生数据复制
        // 参数起点，参数有多长
        // 前五个
        ByteBuf f1 = byteBuf.slice(0, 5);

        // 引用计数加一，防止 byteBuf被释放，导致数据清空
        f1.retain();

        // 切完之后，长度有限制，不允许向结果写入更多的内容
        // writerIndex(5) + minWritableBytes(1) exceeds maxCapacity(5): UnpooledSlicedByteBuf(ridx: 0, widx: 5, cap: 5/5, unwrapped: PooledUnsafeDirectByteBuf(ridx: 0, widx: 10, cap: 10))
        //f1.writeByte('x');

        TestByteBufUtil.log(f1);
        // 后五个
        ByteBuf f2 = byteBuf.slice(5, 5);
        TestByteBufUtil.log(f2);
        f2.retain();

        // 证明是同一块儿内存，修改一个其他也跟着改变
//        f1.setByte(0, 'k');
//        log.info(" ====== ");
//        TestByteBufUtil.log(byteBuf);
//        TestByteBufUtil.log(f1);

        // 释放原有 byteBuf, 原来的都释放了，那么切片的肯定也没数据了
        log.info("释放原有 byteBuf 内存");
        byteBuf.release();
        TestByteBufUtil.log(f1);

        // 自己释放
        f1.release();
        f2.release();

    }

}
