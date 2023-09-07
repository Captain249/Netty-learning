package com.szj.learning.netty.serialization.protobuf;

import com.szj.learning.common.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/7 7:30 下午
 * @Description
 */
public class ProtobufServer {

    private void run() throws InterruptedException {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boos, worker).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder())// Varint32 编码的长度前缀()
                                    .addLast(new ProtobufDecoder(UserProto.User.getDefaultInstance()))// 解码
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())// 发送的 protobuf 消息前面添加一个长度字段，这个长度字段使用 Varint32 编码
                                    .addLast(new ProtobufEncoder())// 编码
                                    .addLast(new ProtobufServerHandler(ch));
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
        new ProtobufServer().run();
    }

}
