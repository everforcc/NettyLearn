/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-10 20:00
 * Copyright
 */

package cn.cc.nio.bytebuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 2.1 buffer简介
 */
@Slf4j
public class Test1ByteBuffer {

    /**
     * 1. 向 buffer 写入数据 ,channel.read(buffer)
     * 2. 调用 flip() 切换至读模式
     * 3. 从 buffer 读取数据，例如调用 buffer.get()
     * 4. 调用 clear() 或compact() 切换至写模式
     * 5. 重复 1-4 步骤
     */
    public static void main(String[] args) {
        // 获取FileChannel
        // 1. 输入输出流 2. RandomAccessFile
        try (FileChannel channel = new FileInputStream("file/data.txt").getChannel()) {
            // 2. 缓冲区
            // 10个字节作为缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            // 从 channel 读取数据.向buffer写入
            while (true) {
                // The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream
                int len = channel.read(byteBuffer); // 如果是-1就表示没了
                log.debug("读取到的字节数: {}", len);
                if (-1 == len) {
                    break;
                }
                // 打印buffer的内容
                // buffer切换读写模式
                byteBuffer.flip(); // buffer切换到读模式
                while (byteBuffer.hasRemaining()) {// 检查是否还有剩余的数据
                    byte b = byteBuffer.get(); // 一次读取一个字节
                    log.debug("读到的字节: {}", (char) b);
                }
                byteBuffer.clear(); // buffer切换为写模式
            }

        } catch (IOException ignored) {

        }


    }

}
