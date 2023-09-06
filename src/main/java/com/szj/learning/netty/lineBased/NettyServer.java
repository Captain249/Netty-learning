package com.szj.learning.netty.lineBased;

import com.szj.learning.common.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/5 7:12 下午
 * @Description
 */
public class NettyServer {

    public void run() throws InterruptedException {
        // 服务端NIO线程组
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast(new LineBasedFrameDecoder(1024))// 需要识别换行符才能结束
                                    .addLast(new StringDecoder())// 将收到的对象转成字符串
                                    .addLast(new ServerHandler());
                        }
                    });
            // 绑定端口，同步等待成功
            ChannelFuture f = bootstrap.bind(Constant.SERVER_PORT).sync();
            // 等待服务端监听端口关闭，这行代码可以防止程序退出。
            f.channel().closeFuture().sync();
        } finally {// 优雅退出
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().run();
    }

}
