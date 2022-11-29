/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-18 15:22
 * Copyright
 */

package cn.cc.nio.net;

import cn.cc.nio.utils.ByteBufferUtil;
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
 * <p>
 * p30 用完key为什么要 remove
 * p31 处理客户端断开问题
 * p32-p35 处理消息边界
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
     *
     * 问题
     * 1. ByteBuffer 空间不够要扩容
     * 2. ByteBuffer 不能做为局部变量
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
                    // 不光 迭代器 要删除，具体的事件也要处理掉，要不然又会重新加入集合中

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
                        try {
                            SocketChannel channel = (SocketChannel) key.channel(); // 拿到触发事件的 channel
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            int read = channel.read(buffer);
                            // 不管是不是断开都会发生 read 事件
                            // 如果是正常断开 read 的返回值是 -1
                            if (-1 == read) {
                                log.info("正常关闭: -1");
                                key.cancel();
                            } else {
//                                buffer.flip();
//                                //ByteBufferUtil.debugRead(buffer);
//                                System.out.println(Charset.defaultCharset().decode(buffer));
                                // 读差分后的消息
                                split(buffer);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 6. 如果客户端强行关闭，必须处理异常
                            log.error("强行关闭");
                            key.cancel(); // 给他取消掉，要不会重新加入集合中
                        }
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 证明找到一条完整消息
            // 因为是get(i) 所以不改变position的值
            if (source.get(i) == '\n') {
                // 这个时候,posetion位置还没有变化
                int length = i + 1 - source.position();
                // 把完整的消息,存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    // 如果是 \n 那么进来后会改变位置
                    target.put(source.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        // 未读完的压缩过去
        source.compact();
    }

}
