package com.szj.learning.basics.aio;

import com.szj.learning.common.Constant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class AIOServer {

    private final AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AIOServer() throws IOException {
        asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
        asynchronousServerSocketChannel.bind(new InetSocketAddress(Constant.LOCAL_HOST, Constant.SERVER_PORT));
    }

    private void run() throws InterruptedException {

        // attachment 相当于是传参给后面回调接口 CompletionHandler，这里不需要
        asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                // 这是必要的一步，继续接受下一个 client 的 accept
                asynchronousServerSocketChannel.accept(attachment, this);

                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                // 第一个 byteBuffer 是指把数据读给 byteBuffer
                // 第二个 byteBuffer 是指把 attachment 传参给后面的 ReadCompletionHandler 中的 A 泛型对象。
                // 其实和这里把 result 传参进去作为 ReadCompletionHandler 的全局变量差不多意思，只是 A 传的是局部变量
                result.read(byteBuffer, byteBuffer, new ReadCompletionHandler(result));
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                // 继续接受下一个 client 的 accept
                asynchronousServerSocketChannel.accept(attachment, this);
            }
        });

        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AIOServer aioServer = new AIOServer();
        aioServer.run();
    }
}
