package com.szj.learning.aio;

import static com.szj.learning.common.Constant.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/5 11:44 上午
 * @Description
 */
public class AIOClient implements CompletionHandler<Void, AIOClient> {

    private final AsynchronousSocketChannel client;

    public AIOClient() throws IOException {
        this.client = AsynchronousSocketChannel.open();
    }

    private void run() throws InterruptedException {
        // 连接的异步回调方法直接调用本类的 completed 和 failed
        client.connect(LOCAL_ADDRESS, this, this);
        TimeUnit.SECONDS.sleep(10);
    }

    @Override
    public void completed(Void result, AIOClient attachment) {
        // 连接成功后往服务端发一条消息
        byte[] bytes = CLIENT_SEND_MSG.getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        client.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                // 如果没发完继续发
                if (attachment.hasRemaining()) {
                    client.write(byteBuffer, byteBuffer, this);
                } else {
                    // 客户端已经发完消息，需要接收服务端响应
                    ByteBuffer byteBufferForRead = ByteBuffer.allocate(1024);
                    client.read(byteBufferForRead, byteBufferForRead, new CompletionHandler<Integer, ByteBuffer>() {
                        @SneakyThrows
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            if (result > 0) {
                                // attachment 上一个状态是读，要写出来
                                attachment.flip();
                                byte[] readBytes = new byte[attachment.remaining()];
                                attachment.get(readBytes);
                                String body = new String(readBytes, StandardCharsets.UTF_8);
                                System.out.println(body);
                                if (attachment.hasRemaining()) {
                                    client.read(attachment, attachment, this);
                                }
                            } else if (result < 0) {
                                System.out.println(SERVER_OFF);
                                client.close();
                            }
                        }

                        @SneakyThrows
                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            System.out.println(SOCKET_DISCONNECT);
                            client.close();
                        }
                    });
                }
            }

            @SneakyThrows
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println(SOCKET_DISCONNECT);
                client.close();
            }
        });

    }

    @SneakyThrows
    @Override
    public void failed(Throwable exc, AIOClient attachment) {
        System.out.println(SOCKET_DISCONNECT);
        client.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AIOClient aioClient = new AIOClient();
        aioClient.run();
    }
}
