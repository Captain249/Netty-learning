package com.szj.learning.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/8 5:43 下午
 * @Description
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker wsHandshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    // 传统的 HTTP 接入
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        System.out.println("[handleHttpRequest]");
        if (req.decoderResult().isFailure() || !"websocket".equals(req.headers().get("Upgrade"))) {
            sendErr(ctx, 500, "不是需要建立 websocket 的 http 请求");
            return;
        }
        // 构建 WebSocket 握手器，
        // 第二个参数是子协议（subprotocol），这里为null，表示没有指定子协议。
        // 第三个参数是是否支持扩展，这里是false，表示不支持WebSocket扩展。
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
        // 创建一个 WebSocket 握手器，用于从请求中提取 WebSocket 协议的相关信息。
        wsHandshaker = wsFactory.newHandshaker(req);
        // 无法创建 WebSocket 握手器，通常是因为客户端请求的 WebSocket 版本不受支持或者请求格式不正确。
        // 在这种情况下，发送一个不支持的版本响应给客户端，告诉客户端连接无法建立。
        if (wsHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            wsHandshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 关闭帧用于在客户端和服务器之间协商关闭连接
        if (frame instanceof CloseWebSocketFrame) {
            wsHandshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 心跳帧
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("frame types not supported");
        }
        String msg = ((TextWebSocketFrame) frame).text();
        System.out.println("[服务端收到 webSocket 消息] - " + msg);
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务端回复信息"));
    }

    private void sendErr(ChannelHandlerContext ctx, int code, String msg) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                new HttpResponseStatus(code, msg)
        );
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
        response.content().writeBytes(byteBuf);
        byteBuf.release();
        // 设置响应头部
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        // 发送响应
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
