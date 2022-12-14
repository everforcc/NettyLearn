/**
 * @Description
 * @Author everforcc
 * @Date 2022-12-07 10:25
 * Copyright
 */

package cn.cc.netty.base.c3futurepromise;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 1. 测试jdk中的 future
 */
@Slf4j
public class Test1JdkFuture {

    public static void main(String[] args) {
        // 1. 线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // 2. 提交任务
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(2000);
                return 50;
            }
        });
        // 3. 主线程通过 future 获得结果
        try {
            log.debug("准备获取结果");
            Integer integerResult = future.get();
            log.debug("future 获取结果: {}", integerResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
