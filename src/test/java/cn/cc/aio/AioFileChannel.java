/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-29 16:08
 * Copyright
 */

package cn.cc.aio;

import cn.cc.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class AioFileChannel {

    public static void main(String[] args) {
        //
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("file/data.txt"), StandardOpenOption.READ)) {
            // 1. 结果，
            // 2. 文件从哪个位置开始
            // 3. 附件
            // 4. 回调对象
            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.debug("read begin ... ");
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {

                // 守护线程，如果其他线程运行完了，那么他也会结束
                // 当文件正确读取完毕后
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed ... {}", result);
                    attachment.flip();
                    ByteBufferUtil.debugAll(attachment);
                }

                // 当文件读取失败时
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {

                }
            });
            log.debug("read end ... ");
            // 保证守护线程运行完
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
