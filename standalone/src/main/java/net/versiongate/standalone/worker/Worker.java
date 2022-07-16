package net.versiongate.standalone.worker;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.versiongate.standalone.Server;
import net.versiongate.standalone.connection.ConnectionContext;

public class Worker {
    private final Selector selector = Selector.open();
    private final Map<SocketChannel, ConnectionContext> connectionContexts = new ConcurrentHashMap<>();

    public Worker() throws IOException {
        WorkerThread.start((context) -> {
            try {
                this.tick(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleConnection(SocketChannel client, SocketChannel server) throws IOException {
        final ConnectionContext clientContext = new ConnectionContext(server);
        final ConnectionContext serverContext = new ConnectionContext(client);

        this.connectionContexts.put(client, clientContext);
        this.connectionContexts.put(server, serverContext);

        this.register(client);
        this.register(server);

        this.selector.wakeup();
    }

    private void tick(WorkerContext context) throws IOException {
        this.selector.select();

        final Set<SelectionKey> keys = this.selector.selectedKeys();
        for (final SelectionKey key : keys) {
            final SocketChannel channel = (SocketChannel) key.channel();
            if (!channel.isOpen()) {
                continue;
            }

            if (!key.isReadable()) {
                continue;
            }

            final ConnectionContext connectionContext = this.connectionContexts.get(channel);
            try {
                final ByteBuffer readBuffer = context.getReadBuffer();
                if (channel.read(readBuffer) == -1) {
                    throw new IOException("Disconnected");
                }

                readBuffer.flip();
                connectionContext.write(readBuffer);
            } catch (IOException e) {
                e.printStackTrace();

                channel.close();
                this.connectionContexts.remove(channel);

                final SocketChannel target = connectionContext.getTarget();
                target.close();
                this.connectionContexts.remove(target);
            } finally {
                context.clearBuffers();
            }
        }

        keys.clear();
    }

    private void register(SocketChannel channel) throws IOException {
        channel.configureBlocking(false);
        channel.register(this.selector, SelectionKey.OP_READ);

        final Socket socket = channel.socket();
        socket.setSendBufferSize(Server.SOCKET_BUFFER_SIZE);
        socket.setReceiveBufferSize(Server.SOCKET_BUFFER_SIZE);
        socket.setTcpNoDelay(true);
    }
}
