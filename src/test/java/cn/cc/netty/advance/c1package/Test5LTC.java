/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-14 20:40
 * Copyright
 */

package cn.cc.netty.advance.c1package;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 5. 测试 LTC 编解码
 */
public class Test5LTC {

    /**
     * 使用测试工具
     * EmbeddedChannel
     */
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                // 测试不剥离,无调整
                //new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0),
                // 测试不剥离,调整
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 1, 0),
                // 测试剥离,调整
                //new LengthFieldBasedFrameDecoder(1024, 0, 4, 1, 4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        //  4 个字节的内容长度， 实际内容
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer, "Hello, world");
        send(buffer, "Hi!");
        channel.writeInbound(buffer);
    }

    private static void send(ByteBuf buffer, String content) {
        byte[] bytes = content.getBytes(); // 实际内容
        int length = bytes.length; // 实际内容长度
        // todo 大端表示法
        // 4个字节
        buffer.writeInt(length);
        // 自定义的间隔，例如版本号
        buffer.writeByte(1);
        // 具体内容
        buffer.writeBytes(bytes);
    }

}
