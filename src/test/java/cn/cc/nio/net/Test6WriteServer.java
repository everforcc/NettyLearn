/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-28 13:45
 * Copyright
 */

package cn.cc.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * 写入内容过多
 * 处理可写事件,一次写不完分多次
 * 连接后就写，写不完的，保存一个写事件，然后完事儿就去写
 */
public class Test6WriteServer {

    public static void main(String[] args) {

        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            ssc.bind(new InetSocketAddress(8080));
            while (true) {
                selector.select();
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        //key.channel();
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false);
                        SelectionKey scKey = sc.register(selector, 0, null);
                        scKey.interestOps(SelectionKey.OP_READ);

                        // 1. 向客户端发送大量数据
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < 10000000; i++) {
                            stringBuilder.append("a");
                        }
                        ByteBuffer buffer = Charset.defaultCharset().encode(stringBuilder.toString());
                        // 2. write 代表 实际写入的字节数
//                        while (buffer.hasRemaining()) {
//                            int write = sc.write(buffer);
//                            System.out.println("write: " + write);
//                        }

                        int write = sc.write(buffer);
                        System.out.println("write: " + write);

                        // 3. 判断是否有剩余内容
                        if (buffer.hasRemaining()) {
                            // 4. 关注可写事件
                            // 保持原事件不消失 +/|
                            scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                            // 5. 要把未写完的数据挂到 scKey 上面
                            scKey.attach(buffer);
                        }
                    } else if (key.isWritable()) {
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        SocketChannel sc = (SocketChannel) key.channel();
                        int write = sc.write(buffer);
                        System.out.println("write: " + write);
                        // 6. 数据写完了,清理操作
                        if (!buffer.hasRemaining()) {
                            // 需要清除buffer
                            key.attach(null);
                            // 不需要在关注可写事件
                            key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        }
                    }
                }
            }
        } catch (ClosedChannelException closedChannelException) {
            closedChannelException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

}
