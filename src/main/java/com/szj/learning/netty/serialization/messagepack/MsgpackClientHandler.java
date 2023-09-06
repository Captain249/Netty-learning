package com.szj.learning.netty.serialization.messagepack;

import com.szj.learning.netty.serialization.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

public class MsgpackClientHandler extends ChannelInboundHandlerAdapter {

    private final SocketChannel socketChannel;

    public MsgpackClientHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        User[] users = new User[]{new User("沈卓钧", 1), new User("粽子", 2)};
        for (User user : users) {
            ctx.write(user);
        }
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.printf("收到服务端消息:" + msg.toString());
        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        socketChannel.close();
    }
}
