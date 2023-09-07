package com.szj.learning.netty.serialization.messagepack;

import com.szj.learning.netty.serialization.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/7 10:20 上午
 * @Description
 */
public class MsgpackServerHandler extends ChannelInboundHandlerAdapter {

    private SocketChannel socketChannel;

    public MsgpackServerHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端收到消息: " + msg.toString());
        // 给一个回包
        ctx.writeAndFlush(new User("机器人回复", 9999));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        socketChannel.close();
    }
}
