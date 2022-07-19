package net.versiongate.api.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

public interface IConnectionManager {

    /**
     * Called when a platform injects its codec into the {@link ChannelPipeline} of a connection. This will then create a {@link IConnection} for the correct
     * user.
     *
     * @param channel The channel representing the connection
     */
    IConnection notifyConnection(Channel channel);
}
