package net.versiongate.standalone.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import java.net.SocketAddress;
import net.versiongate.api.connection.IConnection;
import net.versiongate.common.connection.ConnectionManager;

public class StandaloneChannelInitializer extends ChannelInitializer<Channel> {
    private final SocketAddress address;
    private final ConnectionManager connectionManager;

    public StandaloneChannelInitializer(SocketAddress address) {
        this.address = address;
        this.connectionManager = new ConnectionManager();
    }

    @Override
    protected void initChannel(Channel channel) {
        final IConnection connection = this.connectionManager.notifyConnection(channel);
        connection.setProtocolVersion(340);

        channel.pipeline()
            .addLast(new StandaloneEncoder(connection))
            .addLast(new StandaloneDecoder(connection))
            .addLast(new StandaloneFrontendHandler(this.address));
    }
}
