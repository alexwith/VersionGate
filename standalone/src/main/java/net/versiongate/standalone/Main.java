package net.versiongate.standalone;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.versiongate.standalone.netty.NettyServer;

public class Main {

    public static final SocketAddress STANDALONE_ADDRESS = new InetSocketAddress("0.0.0.0", 25566);
    public static final SocketAddress TARGET_ADDRESS = new InetSocketAddress("0.0.0.0", 25565);

    public static void main(String[] args) throws Exception {
        //final NioServer server = new NioServer();
        final NettyServer server = new NettyServer();
        server.start();
    }
}
