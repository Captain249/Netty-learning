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
                            // 序列化的数据本身包含了消息的完整信息，包括数据的长度和类型等。
                            // 反序列化器能够从序列化的数据中恢复出原始的对象，而不需要额外的消息边界信息。
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
