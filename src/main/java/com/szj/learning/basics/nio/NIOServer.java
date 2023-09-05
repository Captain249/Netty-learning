package com.szj.learning.basics.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import com.szj.learning.common.Constant;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/4 4:09 下午
 * @Description
 */
public class NIOServer {

    private static final String SERVER_RSP = "ok";

    private final Selector selector;

    private final ServerSocketChannel serverSocketChannel;

    public NIOServer() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        // 配置 server 端 channel 为非阻塞(实质是设置打开的 fd 为非阻塞 IOUtil.configureBlocking(fd, block))
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(Constant.LOCAL_HOST, Constant.SERVER_PORT), 1024);
        // 在 selector 上注册 ACCEPT 事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void run() throws IOException {
        while (true) {
            // 轮询已就绪的 key，该方法会阻塞，可以设置最多阻塞 1000ms 后返回
            // 这里只有在调用了 select 方法后，才会更新 publicSelectedKeys
            selector.select(1000);
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            // 这里需要把已经处理过的 key remove 掉，所以使用 iterator
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                try {
                    handleKey(key);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 如果 handle 有问题就注销这个 key
                    if (key == null) {
                        return;
                    }
                    key.cancel();
                    if (key.channel() != null) {
                        key.channel().close();
                    }
                }
            }
        }
    }

    private void handleKey(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            return;
        }
        // 客户端连接事件
        handleAccept(key);
        // channel 读事件
        handleRead(key);
    }

    // 处理 accept 事件
    private void handleAccept(SelectionKey key) throws IOException {
        if (!key.isAcceptable()) {
            return;
        }
        SocketChannel socketChannel = serverSocketChannel.accept();
        // 设置为非阻塞
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    // 处理 read 事件
    // 这里有几个经常会搞乱的点，SocketChannel、ByteBuffer这几个对象的读写操作
    // 以读为例，socketChannel.read(readBuffer);（ByteBuffer 是 get），顺着读，socketChannel 读给 readBuffer
    // 写 socketChannel.write(writeBuffer);（ByteBuffer 是 put），逆着写，writeBuffer 写给 socketChannel
    private void handleRead(SelectionKey key) throws IOException {
        if (!key.isReadable()) {
            return;
        }
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // channel 读写都是面向 buffer 的
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int read = socketChannel.read(readBuffer);
        if (read > 0) {
            // 已经从 channel 读取到 readBuffer 里的，现在要从 buffer 里向外输出，要 flip()
            readBuffer.flip();
            // readBuffer.remaining() 是 limit - position 也就是当前已经使用的数据
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            // 最后转为 String
            String body = new String(bytes, StandardCharsets.UTF_8);
            System.out.println("read: " + body);
            // 给客户端一个响应
            byte[] rspBytes = SERVER_RSP.getBytes(StandardCharsets.UTF_8);
            ByteBuffer writeBuffer = ByteBuffer.allocate(rspBytes.length);
            writeBuffer.put(rspBytes);
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
        }
    }

    public static void main(String[] args) throws IOException {
        NIOServer nioServer = new NIOServer();
        nioServer.run();
    }

}
