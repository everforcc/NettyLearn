/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-17 13:57
 * Copyright
 */

package cn.cc.nio.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 1. 数据传输
 */
public class Test1FileChannelTransferTo {

    public static void main(String[] args) {
        try (FileChannel from = new FileInputStream("F:/everforcc/移动硬盘/待处理/test.zip").getChannel();
             FileChannel to = new FileOutputStream("F:/everforcc/移动硬盘/待处理/test1.zip").getChannel()) {
            // 数据传输，效率高，底层会利用操作系统的零拷贝进行优化
            // 传输文件上限，最多 2G
            long size = from.size();
            // left 代表变量还剩余多少字节
            // 最好用 SSD 机械太慢
            for (long left = size; left > 0; ) {
                System.out.println("position: " + (size - left) + " left:" + left);
                left -= from.transferTo((size - left), left, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
