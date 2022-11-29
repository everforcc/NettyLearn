/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-17 11:39
 * Copyright
 */

package cn.cc.nio.bytebuffer;

import cn.cc.nio.utils.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * 2.8 黏包半包
 */
public class Test8ByteBufferExam {

    public static void main(String[] args) {
        // todo
        /*

         Hello,world\n
         I'm zhangsan\n
         How are you?\n

         Hello,world\nI'm zhangsan\nHo
         w are you?\n

         */
        // 黏包
        // 为了效率,如果多条数据, 一次合成一条发送效率高
        // 半包
        // 服务器缓冲区大小限制,假如大小不够,

        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
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
