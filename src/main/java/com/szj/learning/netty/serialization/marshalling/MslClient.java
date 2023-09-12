package com.szj.learning.netty.serialization.marshalling;

import com.szj.learning.common.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/12 2:25 下午
 * @Description
 */
public class MslClient {

    private void connect() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(MslCodecFactory.buildMslDecoder())
                                    .addLast(MslCodecFactory.buildMslEncoder())
                                    .addLast(new MslClientHandler(ch));
                        }
                    });
            ChannelFuture f = bootstrap.connect(Constant.LOCAL_HOST, Constant.SERVER_PORT).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MslClient().connect();
    }

}
