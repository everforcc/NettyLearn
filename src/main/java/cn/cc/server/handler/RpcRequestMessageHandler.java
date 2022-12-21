package cn.cc.server.handler;

import cn.cc.message.RpcRequestMessage;
import cn.cc.message.RpcResponseMessage;
import cn.cc.server.service.HelloService;
import cn.cc.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        log.debug("进入远程调用");
        RpcResponseMessage response = new RpcResponseMessage();
        // 请求相应要对上
        response.setSequenceId(message.getSequenceId());
        try {
//            HelloService service = (HelloService)
//                    ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            Object service = ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());
            log.debug("远程调用结果: {}", invoke);
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getCause().getMessage();
            log.debug("远程调用出错: {}", msg);
            response.setExceptionValue(new Exception("远程调用出错:" + msg));
        }
        ctx.writeAndFlush(response);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "cn.cc.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );
//        HelloService service = (HelloService)
//                ServicesFactory.getService(Class.forName(message.getInterfaceName()));

        // 因为是反射获得的对象，不需要强转也可以
        Object service = ServicesFactory.getService(Class.forName(message.getInterfaceName()));

        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }
}
