package cn.cc.client.handler;

import cn.cc.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    // 要考虑线程安全问题，虽然是共享变量，但是是安全的，所以可以 @ChannelHandler.Sharable
    // todo Promise<?> ? 只能get不能set 除了null
    //                       序号      用来接收结果的 promise 对象       保证线程安全
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("msg: {}", msg);
        // todo PROMISES 是个容器，在多个线程间传递结果
        // 拿到空的 promise,用完之后 PROMISES 就没用了，remove能获取当前对象并删除
        Promise<Object> promise = PROMISES.remove(msg.getSequenceId());
        if (promise != null) {
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if (exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(returnValue);
            }
        }
    }
}
