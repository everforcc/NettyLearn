/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-18 15:32
 * Copyright
 */

package cn.cc.nio.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 2. 客户端
 */
@Slf4j
public class Test6WriteClient {

    public static void main(String[] args) {
        try {
            SocketChannel sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("localhost", 8080));
            log.debug("waiting...");
            // 接收数据
            int count = 0;
            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                count += sc.read(buffer);
                System.out.println(count);
                buffer.clear();
            }
            //System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
