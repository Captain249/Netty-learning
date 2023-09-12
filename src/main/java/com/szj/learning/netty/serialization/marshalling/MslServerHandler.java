package com.szj.learning.netty.serialization.marshalling;


import com.szj.learning.netty.serialization.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/7 7:34 下午
 * @Description
 */
public class MslServerHandler extends ChannelInboundHandlerAdapter {

    private final SocketChannel socketChannel;

    public MslServerHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        User user = (User) msg;
        System.out.println("服务端收到消息:" + user.getName() + " " + user.getUid());
        // 给响应
        User rspUser = new User();
        rspUser.setUid(9999);
        rspUser.setName("机器人回复");
        ctx.writeAndFlush(rspUser);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        socketChannel.close();
    }
}
