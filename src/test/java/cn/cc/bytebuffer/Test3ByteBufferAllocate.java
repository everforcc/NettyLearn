/**
 * @Description
 * @Author everforcc
 * @Date 2022-11-17 10:37
 * Copyright
 */

package cn.cc.bytebuffer;

import java.nio.ByteBuffer;

/**
 * 2.3 buffer分配空间
 */
public class Test3ByteBufferAllocate {

    public static void main(String[] args) {
        // 固定的,不能动态调整

        ByteBuffer bufferAllocate = ByteBuffer.allocate(16);
        // class java.nio.HeapByteBuffer
        // java的堆内存，读写效率较低，会受到垃圾回收(gc)的影响
        System.out.println(bufferAllocate.getClass());

        ByteBuffer byteAllocateDirect = ByteBuffer.allocateDirect(16);
        // class java.nio.DirectByteBuffer
        // 直接内存，读写效率高（少一次拷贝），不会收到GC影响，分配内存的效率低，如果使用不当，会造成内存泄漏
        System.out.println(byteAllocateDirect.getClass());

    }

}
