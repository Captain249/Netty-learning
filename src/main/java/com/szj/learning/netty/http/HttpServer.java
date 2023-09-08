package com.szj.learning.netty.http;

import com.szj.learning.common.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/8 2:08 下午
 * @Description 文件服务器
 */
public class HttpServer {

    private void run() throws InterruptedException {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boos, worker).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpRequestDecoder())
                                    // 将 HTTP 消息的多个部分（如请求头、请求体、响应头、响应体等）聚合成一个完整的 HTTP 消息对象。（单一的FullHttpRequest 或者 FullHttpResponse)
                                    // 聚合的最大消息大小为 65536字节，如果超过会抛出异常
                                    .addLast(new HttpObjectAggregator(65536))
                                    .addLast(new HttpResponseEncoder())
                                    // 允许将数据分成小块（chunks），并逐个块地写入到通道中。用于处理大型文件、数据流或需要逐块处理的数据时。将数据分成块可以降低内存占用，因为不需要一次性将整个数据加载到内存中。
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new HttpFileServerHandler());
                        }
                    });
            ChannelFuture f = bootstrap.bind(Constant.SERVER_PORT).sync();
            f.channel().closeFuture().sync();
        } finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new HttpServer().run();
    }

}
