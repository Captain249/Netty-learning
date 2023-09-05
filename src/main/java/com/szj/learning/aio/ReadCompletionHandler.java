package com.szj.learning.aio;

import static com.szj.learning.common.Constant.*;

import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

// 第一个泛型参数 Integer 是固定的，用于获取字节状态 -1 为对端关闭连接
// 第二个泛型参数为 IO 操作对象，由前面调用它的方法传入
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private final AsynchronousSocketChannel asynchronousSocketChannel;

    public ReadCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    @SneakyThrows
    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {
        if (result == -1) {
            System.out.printf(CLIENT_OFF);
            asynchronousSocketChannel.close();
        }
        // 这里的 byteBuffer 原先是读，现在要写出来到 byte[]
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("服务端收到消息: " + body);

        // 给客户端一个响应
        byte[] bytesForRsp = SERVER_RSP_MSG.getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBufferForRsp = ByteBuffer.allocate(bytesForRsp.length);
        byteBufferForRsp.put(bytesForRsp);
        byteBufferForRsp.flip();

        // byteBufferForRsp 当前是写状态
        asynchronousSocketChannel.write(byteBufferForRsp, byteBufferForRsp, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                // 如果没有发完，继续发送
                // 这种情况可能出现在以下场景中
                //
                if (byteBufferForRsp.hasRemaining()) {
                    System.out.println("没发完继续发送");
                    asynchronousSocketChannel.write(attachment, attachment, this);
                }
            }

            @SneakyThrows
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println(SERVER_OFF);
                asynchronousSocketChannel.close();
            }
        });

    }

    @SneakyThrows
    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.out.println(SOCKET_DISCONNECT);
        asynchronousSocketChannel.close();
    }
}
