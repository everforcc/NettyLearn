/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-29 16:59
 * Copyright
 */

package cn.cc.netty.c2eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * 4. netty 客户端
 */
public class Test3EventLoopClient {

    public static void main(String[] args) {
        // 1./[7](server 6 之后的步骤) 启动器类
        try {
            Channel channel = new Bootstrap()
                    // 2.[8] 添加一些组件 EventLoop
                    .group(new NioEventLoopGroup())
                    // 3.[9] 选择客户端的 channel 实现
                    .channel(NioSocketChannel.class)
                    // 4.[10] 添加处理器
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override //[12] 在连接建立后被调用
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            // 需要一个编码器 [15] 把hello，转为ByteBuf
                            nioSocketChannel.pipeline().addLast(new StringEncoder());
                        }
                    })
                    // 5.[11] 连接到服务器
                    .connect(new InetSocketAddress("localhost", 8080))
                    // 阻塞方法，直到连接建立
                    .sync() // [13]
                    // 代表连接对象
                    .channel(); // [14]
            System.out.println(channel);
            // todo 打断点可以设置主线程还是全部线程
            // All Thread
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
