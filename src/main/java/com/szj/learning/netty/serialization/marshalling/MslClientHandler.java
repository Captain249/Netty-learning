package com.szj.learning.netty.serialization.marshalling;

import com.szj.learning.netty.serialization.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

public class MslClientHandler extends ChannelInboundHandlerAdapter {

    private final SocketChannel socketChannel;

    public MslClientHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        User szj = new User("沈卓钧", 1);
        for (int i = 0; i < 100; i++) {
            ctx.writeAndFlush(szj);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到服务端消息:" + msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        socketChannel.close();
    }
}
