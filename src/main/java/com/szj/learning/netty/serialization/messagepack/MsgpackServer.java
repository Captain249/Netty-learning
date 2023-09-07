package com.szj.learning.netty.serialization.messagepack;

import com.szj.learning.common.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/7 10:14 上午
 * @Description
 */
public class MsgpackServer {

    private void run() throws InterruptedException {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,0,2,0,2))
                                    .addLast(new MsgpackDecoder())
                                    .addLast(new LengthFieldPrepender(2))
                                    .addLast(new MsgpackEncoder())
                                    .addLast(new MsgpackServerHandler(ch));
                        }
                    });
            ChannelFuture f = bootstrap.bind(Constant.SERVER_PORT).sync();
            f.channel().closeFuture().sync();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MsgpackServer().run();
    }

}
