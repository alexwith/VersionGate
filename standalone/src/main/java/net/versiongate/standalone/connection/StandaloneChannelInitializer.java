package net.versiongate.standalone.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.SocketAddress;
import net.versiongate.api.connection.IConnection;
import net.versiongate.common.connection.ConnectionManager;
import net.versiongate.standalone.netty.FrameDecoder;
import net.versiongate.standalone.netty.FrameEncoder;
import net.versiongate.standalone.netty.StandaloneDecoder;
import net.versiongate.standalone.netty.StandaloneEncoder;

public class StandaloneChannelInitializer extends ChannelInitializer<Channel> {
    private final SocketAddress address;
    private final ConnectionManager connectionManager;

    private static final int READ_TIMEOUT = 30; // seconds

    public static final String FRAME_DECODER_NAME = "versiongate-frame-decoder";
    public static final String FRAME_ENCODER_NAME = "versiongate-frame-encoder";

    public StandaloneChannelInitializer(SocketAddress address) {
        this.address = address;
        this.connectionManager = new ConnectionManager();
    }

    @Override
    protected void initChannel(Channel channel) {
        final IConnection connection = this.connectionManager.notifyConnection(channel);
        connection.setProtocolVersion(340);

        channel.pipeline()
            .addLast(FRAME_DECODER_NAME, new FrameDecoder())
            .addLast(new ReadTimeoutHandler(READ_TIMEOUT))
            .addLast(FRAME_ENCODER_NAME, new FrameEncoder())
            .addLast(new StandaloneDecoder(channel, connection))
            .addLast(new StandaloneEncoder(connection))
            .addLast(new StandaloneConnection(this.address));
    }
}