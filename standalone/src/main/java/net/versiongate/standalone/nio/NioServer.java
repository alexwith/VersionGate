package net.versiongate.standalone.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import net.versiongate.standalone.Main;
import net.versiongate.standalone.nio.worker.Worker;

/**
 * Credit to TheMode for inspiration for this simple proxy base (https://github.com/TheMode/Paxy)
 */
public class NioServer {
    private final Worker worker;

    public static final int SOCKET_BUFFER_SIZE = 262143;

    public NioServer() throws IOException {
        this.worker = new Worker();
    }

    public void start() throws IOException {
        final Selector selector = Selector.open();
        final ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(Main.STANDALONE_ADDRESS);
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        serverSocket.socket().setReceiveBufferSize(SOCKET_BUFFER_SIZE);

        System.out.println("Server started");

        while (true) {
            this.tick(selector, serverSocket);
        }
    }

    private void tick(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        selector.select();

        final Set<SelectionKey> keys = selector.selectedKeys();
        for (final SelectionKey key : keys) {
            if (!key.isAcceptable()) {
                continue;
            }

            final SocketChannel client = serverSocket.accept();
            final SocketChannel server = SocketChannel.open(Main.TARGET_ADDRESS);

            this.worker.handleConnection(client, server);

            System.out.println("New connection");
        }

        keys.clear();
    }
}
