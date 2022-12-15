package cn.cc.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * 自己提供一个无参构造
     * 保证服务端和客户端一致
     */
    public ProcotolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
