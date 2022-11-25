/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-18 15:22
 * Copyright
 */

package cn.cc.net;

import cn.cc.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 1. 服务端-阻塞
 */
@Slf4j
public class Test1ServerBlock {

    public static void main(String[] args) {
        singleThreadNio();
    }

    /**
     * 阻塞模式下，单线程，同时只能操作一个
     */
    public static void singleThreadNio() {
        // 使用 nio 来理解阻塞模式，单线程
        try {
            // 0. 全局对象
            ByteBuffer buffer = ByteBuffer.allocate(16);
            // 1. 创建了服务器
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // 2. 绑定监听端口
            ssc.bind(new InetSocketAddress(8080));
            // 3. 建立连接集合
            List<SocketChannel> channels = new ArrayList<>();
            while (true) {
                // 4. accept，建立与客户端的连接 SocketChannel 用来与客户端之间通信
                log.debug("connecting...");
                SocketChannel sc = ssc.accept();// 阻塞方法
                log.debug("connected... {}", sc);
                channels.add(sc);
                // 5. 接收客户端发送的数据
                for (SocketChannel channel : channels) {
                    log.debug("before read... {}", channel);
                    channel.read(buffer); // 阻塞方法,线程停止运行
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    log.debug("after read... {}", channel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
