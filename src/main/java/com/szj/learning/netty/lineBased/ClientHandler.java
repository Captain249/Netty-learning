package com.szj.learning.netty.lineBased;

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
        for (int i = 0; i < 100; i++) {
            byte[] bytes = (Constant.CLIENT_SEND_MSG + Constant.STR_END).getBytes(StandardCharsets.UTF_8);
            ByteBuf buffer = Unpooled.buffer(bytes.length);
            buffer.writeBytes(bytes);
            ctx.writeAndFlush(buffer);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("客户端收到响应: " + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
