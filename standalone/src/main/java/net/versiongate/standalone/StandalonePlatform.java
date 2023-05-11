package net.versiongate.standalone;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.common.platform.Platform;
import net.versiongate.standalone.handler.StandaloneChannelInitializer;

public class StandalonePlatform extends Platform {
    private static final SocketAddress PROXY_ADDRESS = new InetSocketAddress("localhost", 25566);
    private static final SocketAddress TARGET_ADDRESS = new InetSocketAddress("localhost", 25565);

    public StandalonePlatform() {
        super(null);

        this.getGateManager().setProtocolVersion(ProtocolVersion.VERSION1_8);

        try {
            this.createConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getProtocolVersion() {
        return 47;
    }

    private void createConnection() throws Exception {
        final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new StandaloneChannelInitializer(TARGET_ADDRESS))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.AUTO_READ, false)
                .bind(PROXY_ADDRESS)
                .sync()
                .channel()
                .closeFuture()
                .sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}