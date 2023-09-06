package com.szj.learning.netty.fixLength;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/5 7:38 下午
 * @Description
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    // 测试的时候，用 telnet 测试发送消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("服务端收到消息: " + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
