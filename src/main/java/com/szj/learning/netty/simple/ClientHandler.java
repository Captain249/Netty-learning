package com.szj.learning.netty.simple;

import java.nio.charset.StandardCharsets;

import com.szj.learning.common.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/5 8:17 下午
 * @Description
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 循环为了验证简单Netty通信时的拆包/粘包问题
        // 服务端收到的消息中，有收到不同倍数长度的CLIENT_SEND_MSG，发生了粘包
        for (int i = 0; i < 100; i++) {
            byte[] bytes = Constant.CLIENT_SEND_MSG.getBytes(StandardCharsets.UTF_8);
            ByteBuf buffer = Unpooled.buffer(bytes.length);
            buffer.writeBytes(bytes);
            ctx.writeAndFlush(buffer);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
        // 拆包/粘包情况下，客户端只收到一条服务端的响应，发生了粘包
        System.out.println("客户端收到响应: " + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
