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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 4. 服务端-selector
 */
@Slf4j
public class Test4ServerSelector {

    public static void main(String[] args) {
        // 非阻塞
        singleThreadNio();
    }

    /**
     * selector
     * <p>
     * 事件
     * accept       ServerSocketChannel 独有的事件，会在有连接请求时触发
     * connect      客户端，连接建立后触发的事件
     * read         可读事件，有数据了
     * write        可写事件，
     */
    public static void singleThreadNio() {

        // 使用 nio 来理解阻塞模式，单线程
        try {
            // 1. 创建 selector ，可以管理多个 channel
            Selector selector = Selector.open();

            ServerSocketChannel ssc = ServerSocketChannel.open();
            // 表示非阻塞模式,默认为 true
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(8080));

            // 2. 建立 channel 和 selector 之间的联系，注册
            // selectionKey 就是将来事件发生后，通过他可以知道事件和哪个channel发生的事件
            SelectionKey sscSelectionKey = ssc.register(selector, 0, null);
            // 指名这个key只关注 accept 事件
            sscSelectionKey.interestOps(SelectionKey.OP_ACCEPT);
            log.debug("redister key:{}", sscSelectionKey);

            while (true) {
                // 3. 调用 select 方法，没有事件发生，线程就阻塞
                // 有事件发生了，就继续向下运行处理事件
                // 在事件未处理时，它不会阻塞。事件发生后，要么处理，要么取消，不能置之不理
                selector.select();

                // 4. 处理事件
                // 拿到所有可读可写
                // selectedKeys 内部包含了所有发生的事件
                //Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                // selectedKeys() selector 会在发生事件后，向集合中加入 key，但不会删除
                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();// accept， read
                while (selectionKeyIterator.hasNext()) {
                    SelectionKey key = selectionKeyIterator.next();
                    // 用完一个key就要删除，集合不会主动删除，否则下次还会再次处理，但是没有事件，就会报错
                    selectionKeyIterator.remove();

                    log.debug("key:{}", key);
                    // 5. 区分事件类型
                    if (key.isAcceptable()) { // 如果是 accept 事件
                        // 拿到发生了事件的 channel
                        // 处理事件
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        // 注册进 selector
                        SelectionKey scKey = sc.register(selector, 0, null);
                        // 关注读事件
                        scKey.interestOps(SelectionKey.OP_READ);
                        // 因为这个是服务 socketChannel 所以多个客户端连接打印的都一样
                        log.debug("{}", sc);
                        // 取消事件
                        //key.cancel();
                    } else if (key.isReadable()) { // read 事件
                        SocketChannel channel = (SocketChannel) key.channel(); // 拿到触发事件的 channel
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        channel.read(buffer);
                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
