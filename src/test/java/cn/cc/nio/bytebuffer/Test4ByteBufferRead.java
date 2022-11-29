/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-17 10:56
 * Copyright
 */

package cn.cc.nio.bytebuffer;

import cn.cc.nio.utils.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * 2.4 测试读方法
 */
public class Test4ByteBufferRead {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        // 切换至读模式
        buffer.flip();

        //tTewind(buffer);

        //tMarkReset();

        tGetI(buffer);
    }

    /**
     * 从头开始读
     */
    private static void tTewind(ByteBuffer buffer) {
        // 读四个字节
        buffer.get(new byte[4]);
        ByteBufferUtil.debugAll(buffer);

        // 从头开始读
        buffer.rewind();
        buffer.get();
        System.out.println((char) buffer.get());
    }

    /**
     * 标记和重置位置
     */
    private static void tMarkReset(ByteBuffer buffer) {
        // mark & reset
        // mark 做一个标记，记录posetion位置
        // reset 是将 posetion重置到 mark 的位置
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.mark(); // 加标记
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.reset(); // 将 position 重置到索引 2
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
    }

    /**
     * 测试geti
     * 不会影响读索引的位置
     */
    private static void tGetI(ByteBuffer buffer) {
        System.out.println((char) buffer.get(3));
        ByteBufferUtil.debugAll(buffer);
    }

}
