package com.szj.learning.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import com.szj.learning.common.Constant;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/4 7:42 下午
 * @Description
 */
public class NIOClient {

    private static final String CLIENT_REQ_MSG = "hello";

    private final Selector selector;

    private final SocketChannel socketChannel;

    public NIOClient() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open();
        // 设置非阻塞
        socketChannel.configureBlocking(false);
    }

    public void run() throws IOException {
        doConnect();
        while (true) {
            selector.select(1000);
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
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

    private void doConnect() throws IOException {
        // 由于客户端设置的是非阻塞的，connect 方法会在连接完成之前返回
        if (socketChannel.connect(new InetSocketAddress(Constant.LOCAL_HOST, Constant.SERVER_PORT))) {
            // 注册 read 事件
            socketChannel.register(selector, SelectionKey.OP_READ);
            sendMsgToServer();
        } else {
            // 还没连接完成，需要注册连接完成事件
            // 当服务端返回 TCP syn-ack 消息后，selector 就会轮询到这个事件
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void sendMsgToServer() throws IOException {
        // 写请求到 channel
        byte[] bytes = CLIENT_REQ_MSG.getBytes(StandardCharsets.UTF_8);
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        socketChannel.write(writeBuffer);
    }

    private void handleKey(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            return;
        }
        handleConnect(key);
        handleRead(key);
    }

    private void handleConnect(SelectionKey key) throws IOException {
        // 连接是否操作已经完成
        if (!key.isConnectable()) {
            return;
        }
        // 是否真的建立了连接
        if (socketChannel.finishConnect()) {
            socketChannel.register(selector, SelectionKey.OP_READ);
            sendMsgToServer();
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        if (!key.isReadable()) {
            return;
        }
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int read = socketChannel.read(readBuffer);
        if (read > 0) {
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            String body = new String(bytes, StandardCharsets.UTF_8);
            System.out.println("client get msg: " + body);
        } else if (read < 0) {// <0 对端关闭
            key.cancel();
            socketChannel.close();
        } else {
            // 0 字节忽略
        }
    }

    public static void main(String[] args) throws IOException {
        NIOClient nioClient = new NIOClient();
        nioClient.run();
    }
}
