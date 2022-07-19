package net.versiongate.common.connection;

import io.netty.channel.Channel;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.connection.IConnectionManager;

public class ConnectionManager implements IConnectionManager {

    @Override
    public IConnection notifyConnection(Channel channel) {
        return new Connection(channel);
    }
}
