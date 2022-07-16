package net.versiongate.standalone.nio.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ConnectionContext {
    private final SocketChannel target;

    public ConnectionContext(SocketChannel target) {
        this.target = target;
    }

    public SocketChannel getTarget() {
        return this.target;
    }

    public void write(ByteBuffer buffer) throws IOException {
        while (buffer.remaining() > 0) {
            this.target.write(buffer);
        }
    }
}
