/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-18 15:22
 * Copyright
 */

package cn.cc.nio.netmulti;

import cn.cc.nio.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 1. 多线程优化 selector
 * 多线程执行先后顺序
 */
@Slf4j
public class Test1MultiThreadServer {

    public static void main(String[] args) {
        multiThread();
    }

    /**
     * boss主线程，只用来接收accept
     */
    public static void multiThread() {
        try {
            // 设置线程名
            Thread.currentThread().setName("boss");

            // 0. 全局对象
            ByteBuffer buffer = ByteBuffer.allocate(16);
            // 1. 创建了服务器
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // 2. 绑定监听端口
            ssc.configureBlocking(false);
            Selector boss = Selector.open();
            SelectionKey bossKey = ssc.register(boss, 0, null);
            bossKey.interestOps(SelectionKey.OP_ACCEPT);
            ssc.bind(new InetSocketAddress(8080));

            // 创建固定数量的 worker
            Worker worker_01 = new Worker("worker-0");
            // thread-01
            //worker_01.register();// 初始化，启动worker_01

            while (true) {
                boss.select();
                Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false);
                        log.debug("connected...{}", sc.getRemoteAddress());
                        // 2. 关联selector
                        log.debug("before register...{}", sc.getRemoteAddress());
                        // thread-02
                        // 不一定谁先谁后
                        worker_01.register(sc);// 被 boss线程调用 初始化，启动worker_01
                        //sc.register(worker_01.selectorWorker, SelectionKey.OP_READ, null);// boss
                        log.debug("after  register...{}", sc.getRemoteAddress());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来检测读写事件
     */
    static class Worker implements Runnable {
        private Thread thread;
        private Selector selectorWorker;
        private String name;

        // 用队列来处理，在线程间传递消息
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        // 线程对副本变量进行修改后，其他线程能够立刻同步刷新最新的数值。这个就是可见性。
        private volatile boolean start = false;

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                // 先初始化，否则会报错，因为有先后顺序
                selectorWorker = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;
            }

            // 方式一
            // 向队列添加了任务，但这个任务并没有立刻执行
//            queue.add(() -> {
//                // 放到内部注册，还是在boss线程内执行
//                try {
//                    sc.register(selectorWorker, SelectionKey.OP_READ, null);// boss
//                } catch (ClosedChannelException e) {
//                    e.printStackTrace();
//                }
//            });
//            selectorWorker.wakeup(); // 唤醒 select 方法

            // 方式二,三种情况 selectorWorker.select();
            // 1. 这个位置
            selectorWorker.wakeup(); // 唤醒 select 方法，不管先后顺序，都是能唤醒的，相当于一张票，先后给都一样
            // 2. 这个位置
            sc.register(selectorWorker, SelectionKey.OP_READ, null);// boss
            // 3. 这个位置
        }


        @Override
        public void run() {
            while (true) {
                try {
                    // 如果先执行 select 那就会被阻塞
                    selectorWorker.select(); // worker-0 等待事件阻塞，wakeup

                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run(); // 执行了注册代码
                    }
                    Iterator<SelectionKey> iter = selectorWorker.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();

                        // 只关注读操作
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}", channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            ByteBufferUtil.debugAll(buffer);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
