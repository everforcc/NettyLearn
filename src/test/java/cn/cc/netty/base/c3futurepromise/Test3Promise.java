/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-07 10:51
 * Copyright
 */

package cn.cc.netty.base.c3futurepromise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * 2. 测试 netty future
 */
@Slf4j
public class Test3Promise {

    public static void main(String[] args) {
        // 1. 准备 EventLoop 对象
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        EventLoop eventLoop = nioEventLoopGroup.next();

        // 2. 可以主动 创建 promise 对象，结果容器
        DefaultPromise<Integer> integerDefaultPromise = new DefaultPromise<>(eventLoop);

        // 3. 任意一个线程执行计算，计算完成后，向promise对象填充结果
        new Thread(() -> {
            log.debug("开始计算");
            try {
                int i = 1 / 0;
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                // 异常执行
                integerDefaultPromise.setFailure(e);
            }
            integerDefaultPromise.setSuccess(1500);
        }).start();

        // 4. 接受结果的线程
        // 同步，也可以调整为异步 listener
        log.debug("等待结果...");
        Integer resultInteger = null;
        try {
            resultInteger = integerDefaultPromise.get();
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("出异常了: {}", e.getMessage(), e);
        }
        log.debug("结果是: {}", resultInteger);

    }

}
