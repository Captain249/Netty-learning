package com.szj.learning.aio;

import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

// 第一个泛型参数 Integer 是固定的，用于获取字节状态 -1 为对端关闭连接
// 第二个泛型参数为 IO 操作对象
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private final AsynchronousSocketChannel asynchronousSocketChannel;

    public ReadCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    @SneakyThrows
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        if (result == -1) {
            System.out.printf("客户端断开连接");
            asynchronousSocketChannel.close();
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}
