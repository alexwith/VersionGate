package net.versiongate.standalone.nio.worker;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.versiongate.standalone.nio.NioServer;

public class WorkerContext {
    private final ByteBuffer readBuffer = allocate(NioServer.SOCKET_BUFFER_SIZE);

    public ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

    public void clearBuffers() {
        this.readBuffer.clear();
    }

    private static ByteBuffer allocate(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }
}