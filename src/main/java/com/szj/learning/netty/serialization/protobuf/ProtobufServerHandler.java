package com.szj.learning.netty.serialization.protobuf;


import com.szj.learning.netty.serialization.protobuf.UserProto.User;
import com.szj.learning.netty.serialization.protobuf.UserProto.User.Builder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/7 7:34 下午
 * @Description
 */
public class ProtobufServerHandler extends ChannelInboundHandlerAdapter {

    private io.netty.channel.socket.SocketChannel socketChannel;

    public ProtobufServerHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        User user = (User) msg;
        System.out.println("服务端收到消息:" + user.getName() + " " + user.getUid());
        // 给响应
        Builder builder = User.newBuilder();
        builder.setUid(9999);
        builder.setName("机器人回复");
        User rspUser = builder.build();
        ctx.writeAndFlush(rspUser);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        socketChannel.close();
    }
}
