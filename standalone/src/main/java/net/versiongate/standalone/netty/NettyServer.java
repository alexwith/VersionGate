package net.versiongate.standalone.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.versiongate.standalone.Main;
import net.versiongate.standalone.netty.connection.NettyChannelInitializer;

public class NettyServer {

    public static final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup();
    public static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup();

    public void start() throws InterruptedException {
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap()
                .group(BOSS_GROUP, WORKER_GROUP)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyChannelInitializer())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            final ChannelFuture future = bootstrap.bind(Main.STANDALONE_ADDRESS).sync();
            System.out.println("Server started");

            future.channel().closeFuture().sync();
        } finally {
            BOSS_GROUP.shutdownGracefully();
            WORKER_GROUP.shutdownGracefully();
        }
    }
}
