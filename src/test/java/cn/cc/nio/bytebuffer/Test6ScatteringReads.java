/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-17 11:30
 * Copyright
 */

package cn.cc.nio.bytebuffer;

import cn.cc.nio.utils.ByteBufferUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 2.6 分批读buffer
 * 分散读
 */
public class Test6ScatteringReads {

    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("file/3parts.txt", "r").getChannel()) {
            ByteBuffer a = ByteBuffer.allocate(3);
            ByteBuffer b = ByteBuffer.allocate(3);
            ByteBuffer c = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{a, b, c});
            a.flip();
            b.flip();
            c.flip();
            ByteBufferUtil.debugAll(a);
            ByteBufferUtil.debugAll(b);
            ByteBufferUtil.debugAll(c);
        } catch (IOException ignore) {
        }
    }

}
