package cn.cc.client;

import cn.cc.client.handler.RpcResponseMessageHandler;
import cn.cc.message.RpcRequestMessage;
import cn.cc.protocol.MessageCodecSharable;
import cn.cc.protocol.ProcotolFrameDecoder;
import cn.cc.protocol.SequenceIdGenerator;
import cn.cc.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

@Slf4j
public class RpcClientManager {

    public static void main(String[] args) {
        HelloService service = getProxyService(HelloService.class);
        System.out.println("------------");
        System.out.println(service.sayHello("zhangsan"));
//        System.out.println(service.sayHello("lisi"));
//        System.out.println(service.sayHello("wangwu"));
    }

    // 创建代理类
    public static <T> T getProxyService(Class<T> serviceClass) {
        // 当前的类加载器
        ClassLoader loader = serviceClass.getClassLoader();
        // 代理类要实现的接口，转为数组
        Class<?>[] interfaces = new Class[]{serviceClass};
        // todo 第三个参数，调用方法时的行为
        //                                                            sayHello  "张三"
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            // 1. 将方法调用转换为 消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            // 消息对象
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            // 2. 将消息对象发送出去
            getChannel().writeAndFlush(msg);

            // 3. 准备一个空 Promise 对象，来接收结果             指定 promise 对象异步接收结果线程
            // 从 channel 取出 eventLoop 指定接收结果的线程
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);

//            promise.addListener(future -> {
//                // 线程
//            });

            //promise.sync()
            // 4. 等待 promise 结果
            promise.await(); // todo await不会抛异常，sync会
            if (promise.isSuccess()) {
                // 调用正常
                Object obj = promise.getNow();
                log.debug("promise.getNow(): {}", obj);
                return obj;
            } else {
                // 调用失败
                log.debug("promise.cause(): ", promise.cause());
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }

    // channel
    private static Channel channel = null;
    // 获取channel的锁
    private static final Object LOCK = new Object();

    /**
     * 获取唯一的 channel 对象
     *
     * @return channel
     */
    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) { //  t2
            if (channel != null) { // t1
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    /**
     * 初始化 channel 方法
     */
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            // 同步生成channel
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            // 关闭了 异步才能触发
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
}
