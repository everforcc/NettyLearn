/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-05 14:58
 * Copyright
 */

package cn.cc.netty.c2eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 2. 事件循环组
 */
@Slf4j
public class Test1EventLoop {

    public static void main(String[] args) {
        // 1. 创建事件循环组, 自己设置线程数
        EventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(2); // io 既能处理io事件，普通任务，定时任务
        //EventLoopGroup defaultEventLoop = new DefaultEventLoop(); // 普通任务，定时任务
        //System.out.println(NettyRuntime.availableProcessors());
        //System.out.println(SystemPropertyUtil.getInt("io.netty.eventLoopThreads"));
        //System.out.println(SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));

        // 2. 获取下一个事件循环对象
        // 简单的负载均衡
        System.out.println(nioEventLoopGroup.next());
        System.out.println(nioEventLoopGroup.next());
        System.out.println(nioEventLoopGroup.next());

        // 3. 执行一个任务
//        nioEventLoopGroup.next().submit(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //
//            log.debug("ok");
//        });

        // 4. 定时任务 ,以一定的频率执行任务
        nioEventLoopGroup.next().scheduleAtFixedRate(() -> {
            log.debug("ok");
        }, 0, 1, TimeUnit.SECONDS);

        log.debug("main");
    }

}
