package com.szj.learning.netty.serialization.protobuf;

import com.szj.learning.netty.serialization.protobuf.UserProto.User;
import com.szj.learning.netty.serialization.protobuf.UserProto.User.Builder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/7 7:41 下午
 * @Description
 */
public class ProtobufClientHandler extends ChannelInboundHandlerAdapter {

    private SocketChannel socketChannel;

    public ProtobufClientHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 100; i++) {
            Builder builder = User.newBuilder();
            builder.setName("客户端发包");
            builder.setUid(1);
            User user = builder.build();
            ctx.writeAndFlush(user);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        User user = (User) msg;
        System.out.println("客户端收到响应:" + user.getName() + " " + user.getUid());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        socketChannel.close();
    }
}
