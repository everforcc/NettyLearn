/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-19 16:28
 * Copyright
 */

package cn.cc.netty.optimize;

import cn.cc.config.Config;
import cn.cc.message.LoginRequestMessage;
import cn.cc.message.Message;
import cn.cc.protocol.MessageCodecSharable;
import cn.cc.protocol.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 测试序列化
 */
public class Test1Serializer {

    public static void main(String[] args) {
        MessageCodecSharable CODEC = new MessageCodecSharable();
        LoggingHandler LOGGING = new LoggingHandler();
        // 两次日志是因为，一次序列化过，一次没有
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);

        // 出站消息做编码
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "123");

        // 出站
        //channel.writeOutbound(loginRequestMessage);
        ByteBuf byteBuf = messageToByteBuf(loginRequestMessage);

        // 入站，解码
        channel.writeInbound(byteBuf);
    }

    public static ByteBuf messageToByteBuf(Message msg) {
        int algorithm = Config.getSerializerAlgorithm().ordinal();
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(new byte[]{1, 2, 3, 4});
        out.writeByte(1);
        out.writeByte(algorithm);
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);
        byte[] bytes = Serializer.Algorithm.values()[algorithm].serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        return out;
    }

}
