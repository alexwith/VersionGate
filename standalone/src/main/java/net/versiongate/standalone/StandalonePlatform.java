package net.versiongate.standalone;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.common.platform.Platform;
import net.versiongate.standalone.connection.StandaloneChannelInitializer;

// VERY IMPORTANT: The server must be in offline mode
public class StandalonePlatform extends Platform {
    private static final SocketAddress PROXY_ADDRESS = new InetSocketAddress("localhost", 25566);
    private static final SocketAddress TARGET_ADDRESS = new InetSocketAddress("localhost", 25565);

    public static final KeyPair ENCRYPTION_KEY = createEncryptionKey();

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
                .childOption(ChannelOption.IP_TOS, 0x18)
                .childOption(ChannelOption.AUTO_READ, false);

            final Channel channel = bootstrap.bind(PROXY_ADDRESS).sync().channel();
            System.out.printf("Started VersionGate proxy on %s%n", PROXY_ADDRESS.toString());

            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static KeyPair createEncryptionKey() {
        try {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}