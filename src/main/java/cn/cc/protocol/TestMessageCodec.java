/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-15 16:03
 * Copyright
 */

package cn.cc.protocol;

import cn.cc.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试编码解码问题
 */
@Slf4j
public class TestMessageCodec {

    public static void main(String[] args) {
        // 两个 handler 合二为一了
        EmbeddedChannel channel = new EmbeddedChannel(
                // 按照引入的顺序执行

                // 测试输出日志
                new LoggingHandler(),
                // 解决黏包，半包问题
                // 最大长度，长度字段偏移量，长度字段的长度，长度字段为基准，从头剥离几个字节（不需要，自己解决）
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new MessageCodec()
        );

        // 模拟编码消息
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(loginRequestMessage);

        try {
            // 解码
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
            new MessageCodec().encode(null, loginRequestMessage, buf);
            log.info("入站");

            // 入站
            //channel.writeInbound(buf);

            // 测试半包
            ByteBuf s1 = buf.slice(0, 100);
            ByteBuf s2 = buf.slice(100, buf.readableBytes() - 100);
            // 半包测试的时候不能引入 帧解码器
            s1.retain(); // 引用计数 +1 下面-引用计数器的时候就不会释放了
            channel.writeInbound(s1);  // 调用完会调用 bytebuf 的 release 方法
            channel.writeInbound(s2);
            // java.lang.IndexOutOfBoundsException: readerIndex(16) + length(190) exceeds writerIndex(100):
            //  UnpooledSlicedByteBuf(ridx: 16, widx: 100, cap: 100/100, unwrapped:
            //      PooledUnsafeDirectByteBuf(ridx: 0, widx: 206, cap: 256))

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
