/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-10 20:00
 * Copyright
 */

package cn.cc.nio.bytebuffer;

import cn.cc.nio.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 2.2 buffer读写
 */
@Slf4j
public class Test2ByteBufferReadWrite {

    public static void main(String[] args) {
        // 给10个字节
        ByteBuffer buffer = ByteBuffer.allocate(10);
        // 写入一个字节
        buffer.put((byte) 0x61); // 'a'
        ByteBufferUtil.debugAll(buffer);
        // 写入字节数组
        buffer.put(new byte[]{0x62, 0x63, 0x64}); // bcd
        ByteBufferUtil.debugAll(buffer);

        // 读
        // 不切换的话,只会读取当前的位置,目前是0
        //System.out.println(buffer.get());
        buffer.flip();
        System.out.println(buffer.get());
        // todo 分析工具类
        ByteBufferUtil.debugAll(buffer);

        // 指针会切换,但是原来位置3的数据不会被清零,因为下次写入会从这个位置覆盖
        buffer.compact();
        ByteBufferUtil.debugAll(buffer);

        // 写入字节数组,覆盖上次的历史数据
        buffer.put(new byte[]{0x65, 0x6f});
        ByteBufferUtil.debugAll(buffer);
    }

}
