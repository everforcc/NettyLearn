/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-17 11:09
 * Copyright
 */

package cn.cc.bytebuffer;

import cn.cc.utils.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 2.5 buffer和string转换
 */
public class Test5ByteBufferString {

    public static void main(String[] args) {
        // 1. 字符串转为 butebuffer
        String hello = "hello";
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(hello.getBytes(StandardCharsets.UTF_8));
        ByteBufferUtil.debugAll(buffer);

        // 2. Charset
        // 可以处理字符串和字符集的转换
        // 会自动切换读模式
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode(hello);
        ByteBufferUtil.debugAll(buffer1);

        // 3. wrap 包装
        ByteBuffer buffer2 = ByteBuffer.wrap(hello.getBytes());
        ByteBufferUtil.debugAll(buffer2);

        // 4. 转换为字符串
        // 因为是读模式所以可以直接转换
        // 从buffer转换为str
        String str3 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str3);

        // 没有,因为还是写模式,需要切换为读模式
        //buffer.flip();
        String str = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(str);
    }

}
