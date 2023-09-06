package com.szj.learning.netty.simple;


import java.nio.charset.StandardCharsets;

import com.szj.learning.common.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/5 7:38 下午
 * @Description
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // ByteBuf 类似于NIO中的 ByteBuffer，但不需要手动 flip
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("服务端收到消息: " + body);

        // 类似于 ByteBuffer.allocate + get
        ByteBuf rspByteBuf = Unpooled.copiedBuffer(Constant.SERVER_RSP_MSG.getBytes(StandardCharsets.UTF_8));
        ctx.write(rspByteBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 也可以在 channelRead 中 ctx.writeAndFlush()
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
