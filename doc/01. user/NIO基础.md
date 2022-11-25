<span  style="font-family: Simsun,serif; font-size: 17px; ">

[TOC]

### NIO基础

- non-blocking io
- 非阻塞io
- jdk1.5之后?

#### 1.三大组件

##### 1.1 Channel&Buffer

- Channel 通道
    - 双向通道
- Buffer 缓冲
    - 内存缓冲区

#### 2.ByteBuffer

- 指针
- 读写转换
- 分配内存

#### 3.文件编程

- 1.7的文件工具类
- 观察者模式?
- 优雅的处理文件

#### 4.网络编程

##### 4.1 阻塞模式

- 单线程，阻塞
- 一个阻塞了别的啥都干不了了

##### 4.2 非阻塞模式

- 非阻塞，线程还会继续运行
- 就是会一直循环运行

##### 4.3 selector

- accept 处理事件
- cancel 取消事件
- read
- selectedKeys() 处理完之后必须 remove key否则，下次循环还会再处理一次

### NIO基础-end

- [ ] 看完后看一遍文档,然后再看 netty

</span>