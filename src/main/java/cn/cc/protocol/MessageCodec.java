/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-15 15:15
 * Copyright
 */

package cn.cc.protocol;

import cn.cc.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * byteBuf 和 自定义消息进行转换
 * 两个 handler 合二为一了
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * 编码
     * 出栈前 将 消息 转为 ByteBuf
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 除了正文，需要2的整数倍，常用是16位

        // 1. 4字节的魔数
        out.writeBytes(new byte[]{'c', 'c', 'l', 'l'});
        // 2. 1字节版本
        out.writeByte(1);
        // 3. 序列化算法
        //  0代表jdk，1代表json
        out.writeByte(0);
        // 4. 指令类型
        out.writeByte(msg.getMessageType());
        // 5. 请求序号 双工通信
        out.writeInt(msg.getSequenceId());

        // 没有意义，为了更专业凑够16位
        out.writeByte(0xff);

        // 6. 正文转为字节
        //  jdk序列化
        //  todo 获取内容的字节数组
        // 将对象转成字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();

        // 7. 正文 长度
        out.writeInt(bytes.length);

        // 8. 写入内容
        out.writeBytes(bytes);
    }

    /**
     * 解码
     * 入栈前将 ByteBuf 转换为 消息
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. 魔数
        int magicNum = in.readInt();
        // 2. 版本
        byte version = in.readByte();
        // 3. 序列化类型
        byte serializerType = in.readByte();
        // 4. 消息类型
        byte messageType = in.readByte();
        // 5. 请求序号
        int sequenceId = in.readInt();
        // 无意义字节
        in.readByte();
        // 6. 长度
        int length = in.readInt();
        byte[] bytes = new byte[length];
        // 7. 取出bytes
        in.readBytes(bytes, 0, length);
        if (0 == serializerType) {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Message message = (Message) ois.readObject();
            // magicNum 被当作四个字节的int，其实是四个 byte
            log.debug("magicNum:{}, version:{}, serializerType:{}, messageType:{}, sequenceId:{}, length:{}", magicNum, version, serializerType, messageType, sequenceId, length);
            log.debug("message: {}", message);
            // netty约定，这样才能传给下一个
            out.add(message);
        }
    }
}
