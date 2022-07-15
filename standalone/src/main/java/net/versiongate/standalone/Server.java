package net.versiongate.standalone;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import net.versiongate.standalone.worker.Worker;

/**
 * Credit to TheMode for inspiration for this simple proxy base / copy (https://github.com/TheMode/Paxy)
 */
public class Server {
    private final Worker[] workers = new Worker[WORKER_COUNT];

    private int workerIndex;

    public static final int SOCKET_BUFFER_SIZE = 262143;
    public static final int MAX_PACKET_SIZE = 2097151;

    private static final int WORKER_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    private static final SocketAddress STANDALONE_ADDRESS = new InetSocketAddress("0.0.0.0", 25566);
    private static final SocketAddress TARGET_ADDRESS = new InetSocketAddress("0.0.0.0", 25565);

    public Server() throws IOException {
        for (int i = 0; i < this.workers.length; i++) {
            this.workers[i] = new Worker();
        }
    }

    public void start() throws IOException {
        final Selector selector = Selector.open();
        final ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(STANDALONE_ADDRESS);
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
            final SocketChannel server;
            try {
                server = SocketChannel.open(TARGET_ADDRESS);

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            final Worker worker = this.findWorker();
            worker.handleConnection(client, server);

            System.out.println("New connection");
        }

        keys.clear();
    }

    private Worker findWorker() {
        this.workerIndex = ++this.workerIndex % WORKER_COUNT;
        return this.workers[this.workerIndex];
    }

}
