# Netty-learning
系统学习Netty，基于《Netty权威指南-第2版》，归纳每章节的重点。

## 第1章 Java 的IO 演进之路
### 一、五种IO模型
    
    recvfrom 函数可用于从已连接的套接字接口获取数据。

1. 阻塞I/O模型 
    
   process 在读 recvfrom 操作的两个阶段都是等待的；
   1. process 原地等待 kernel 准备数据；
   2. kernel 准备好数据后，process 继续等待 kernel 将数据 copy 到自己的 buffer；
   3. 在 kernel 完成数据的 copy 后 process 才会从 recvfrom 系统调用中返回。


2. 非阻塞I/O模型

   process 在读 recvfrom操作的第一个阶段是不会block等待的。
   1. 如果 kernel 数据还没准备好，那么 recvfrom 会立刻返回一个 EWOULDBLOCK 错误；
   2. 当kernel 准备好数据后，进入处理的第二阶段的时候，process 会等待 kernel 将数据 copy 到自己的 buffer；
   3. 在 kernel 完成数据的 copy 后 process 才会从 recvfrom 系统调用中返回


3. I/O多路复用

   1. select、poll、epoll 模型。在IO多路复用的时候，process 在两个处理阶段都是 block 等待。 优势在于可以以较少的代价来同时监听处理多个IO；
   2. 将一个或多个 df 传递给 select 或 poll 系统调用，阻塞在 select 操作上，select/poll 顺序扫描 df 是否准备就绪；
   3. epoll 基于事件驱动代替顺序扫描。


4. 信号驱动I/O模型

   1. 系统调用 sigaction 执行信号处理函数，立刻返回（非阻塞）
   2. 当数据准备就绪时，就为该进程生成一个 SIGIO 信号；
   3. 通过信号回调通知应用程序调用 revcfrom 来读取数据。
   

5. 异步I/O 

    内核在整个操作后（包括数据从内核复制到用户自己的缓冲区），通知。


### 二、IO多路复用的优势
- 支持一个进程打开的FD不受限制，仅受限于操作系统的最大文件句柄数

    这个数值和系统的内存关系比较大，1GB内存的机器大约是10W个句柄。如何查看：`cat /proc/sys/fs/file-max`。


- IO效率不会随着FD的数目增加而线性增长

    select/poll 调用会线性扫描全部集合，而 epoll 只会针对"活跃"的 socket 操作。
 

- 使用 mmap 加速内核与用户空间的消息传递
    
    select、poll、epoll 都需要内核把 FD 消息通知给用户空间，epoll 通过内核和用户空间使用同一块内存实现。

## 第2章 NIO入门

    网络套接字 = socket

### BIO通信
服务端每收到客户端的连接请求，都会创建一个**新线程**进行链路处理。
 
代码见
`com.szj.learning.bio.*`

### NIO相关概念

- 缓冲区 Buffer
  ![img.png](img/buffer.png)
    - 原先面向流的IO，读写数据都是直接操作 stream 的，NIO 读写数据都是操作 Buffer 的；
    - 缓冲区实质是一个数组，提供了结构化访问，和维护了读写位置等信息；
    - 网络读写一般使用 ByteBuffer，它提供了更多特有的操作。


- 通道 channel
  ![img.png](img/channel.png)
    - stream 是单向的，channel 是双向的；
    - channel 是全双工的；
    - channel 大致分为用于操作网络的 SelectChannel，和用于操作文件的 FileChannel。
    

- 多路复用器 selector
    - 会不断轮询注册在其上的 channel ，如果在 channel 上发生读写事件，那么这个 channel 就处于就绪状态，会被 selector 轮询出来；
    - JDK 使用 epoll 实现多路复用。

### NIO服务端序列
![img.png](img/nio_server_sequence.png)
### NIO客户端序列
![img.png](img/img.png)

### NIO通信

代码见 com.szj.learning.nio.*