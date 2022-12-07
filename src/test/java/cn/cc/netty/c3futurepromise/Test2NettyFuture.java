/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-07 10:51
 * Copyright
 */

package cn.cc.netty.c3futurepromise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 2. 测试 netty future
 */
@Slf4j
public class Test2NettyFuture {

    public static void main(String[] args) {
        // 1. 线程池
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        EventLoop eventLoop = nioEventLoopGroup.next();
        // netty 下的 future
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(2000);
                return 70;
            }
        });
        // 主线程获取结果

        // 同步
//        try {
//            log.debug("准备获取结果");
//            Integer integerResult = future.get();
//            log.debug("future 获取结果: {}", integerResult);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // todo 类结构分析
        // 异步
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            // 异步接收结果
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                // get 也可以，但是这个时候已经有结果了，所以直接用 getNow就好了
                log.debug("接受结果: {}", future.getNow());
            }
        });

    }

}
