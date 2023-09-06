package com.szj.learning.netty.simple;

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
 * @version 1.0 2023/9/5 8:11 下午
 * @Description
 */
public class NettyClient {

    public void connect() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)// 禁用 Nagle 算法(小数据包合成大数据报，减少了网络拥塞。但是，也可能引入延迟)，这意味着数据将会立即被发送，不管它的大小。
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture f = bootstrap.connect(Constant.LOCAL_HOST, Constant.SERVER_PORT).sync();
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程组
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new NettyClient().connect();
    }

}
