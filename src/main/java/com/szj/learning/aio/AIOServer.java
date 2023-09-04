package com.szj.learning.aio;

import com.szj.learning.common.CommonConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AIOServer {

    private final AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AIOServer() throws IOException {
        asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
        asynchronousServerSocketChannel.bind(new InetSocketAddress(CommonConstant.LOCAL_HOST, CommonConstant.SERVER_PORT));
    }

    private void run() {

        // 1.accept 第一个参数 attachment 是需要附加到IO操作的对象，服务端可以固定为 AsynchronousServerSocketChannel 类型（此参数可以为null）， 第二个参数固定为 CompletionHandler
        // 2.CompletionHandler 的第一个泛型参数是 AsynchronousSocketChannel 类型，第二个泛型参数是 1 中的 attachment 对象
        // 3.completed 处理 accept 成功， failed 是失败。
        asynchronousServerSocketChannel.accept(asynchronousServerSocketChannel, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {
            @Override
            public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {
                // 这是必要的一步，继续接受下一个 client 的 accept
                attachment.accept(attachment, this);
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                // read 第一个参数固定是 buffer（数据读到这个缓冲区）
                // 第二个参数是 IO 操作对象，也是该 buffer
                // 第三个参数是读取操作完成（或失败）时调用的结果处理程序
                result.read(byteBuffer, byteBuffer, new ReadCompletionHandler(result));
            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
                // 继续接受下一个 client 的 accept
                attachment.accept(attachment, this);
            }
        });

    }
}
