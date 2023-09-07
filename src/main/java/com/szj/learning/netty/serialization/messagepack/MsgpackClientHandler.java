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
        // 这里虽然发的是一个数组，但是如果没有做拆包/粘包，只按照一个 User 对象解析接收，只会收到第一个 User
        User[] users = new User[]{new User("沈卓钧", 1), new User("粽子", 2)};
        for (User user : users) {
            ctx.writeAndFlush(user);
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
