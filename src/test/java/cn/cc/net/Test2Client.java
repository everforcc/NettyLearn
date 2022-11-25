/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-18 15:32
 * Copyright
 */

package cn.cc.net;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * 2. 客户端
 */
@Slf4j
public class Test2Client {

    public static void main(String[] args) {
        try {
            SocketChannel sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("localhost", 8080));
            log.debug("waiting...");
            // 按照分隔符拆分
            //sc.write(Charset.defaultCharset().encode("hello\nworld\naaa"));
            sc.write(Charset.defaultCharset().encode("0123456789abcdef3333\n"));
            sc.close();
            //System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
