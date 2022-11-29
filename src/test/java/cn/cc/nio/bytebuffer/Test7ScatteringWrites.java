/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-17 11:33
 * Copyright
 */

package cn.cc.nio.bytebuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 2.7 集中写
 */
public class Test7ScatteringWrites {

    public static void main(String[] args) {
        ByteBuffer a = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b = StandardCharsets.UTF_8.encode("word");
        // 6个字节
        ByteBuffer c = StandardCharsets.UTF_8.encode("你好");

        try (FileChannel channel = new RandomAccessFile("file/words.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{a, b, c});
        } catch (IOException ignore) {
        }
    }

}
