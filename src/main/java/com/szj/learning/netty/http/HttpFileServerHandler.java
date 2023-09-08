package com.szj.learning.netty.http;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/8 2:17 下午
 * @Description
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 用来检查一个 HTTP 请求是否成功解码
        if (request.uri().equals("/err")) {
            sendErr(ctx, 500, "报错");
            return;
        }
        if (request.method() != HttpMethod.GET) {
            sendMethodNotAllowed(ctx);
            return;
        }

        File file = new File("/Users/zhuojunshen/IdeaProjects/study/Netty-learning/src/main/resources/test.txt");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");// 以只读方式打开
        long length = randomAccessFile.length();

        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        // 写响应头
        ctx.write(response);

        // 写文件内容
        // newProgressivePromise 用于创建一个新的 ProgressivePromise。一个 ProgressivePromise 是一个特殊类型的 Promise，它不仅可以表示一个操作的成功或失败，还可以表示操作的进度。
        ChannelFuture sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, length, 8192), ctx.newProgressivePromise());
        // 追踪和报告文件传输的进度和完成状态
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                // 表示文件的总字节数，如果total是未知的（例如，当从一个流中传输内容时，可能不知道内容的总长度），它会是一个负数。
                if (total < 0) {
                    System.err.println(future.channel() + " Transfer progress: " + progress);
                } else {
                    System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                System.out.println(future.channel() + " Transfer complete.");
            }
        });

        // 如果不是 keep-alive，写完文件后关闭连接
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!HttpUtil.isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }

    }

    private void sendMethodNotAllowed(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.METHOD_NOT_ALLOWED,
                Unpooled.copiedBuffer("Method Not Allowed", CharsetUtil.UTF_8)
        );

        // 设置响应头部
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        // 发送响应
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendErr(ChannelHandlerContext ctx, int code, String msg) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                new HttpResponseStatus(code, msg),
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8)
        );
        // 设置响应头部
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        // 发送响应
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
