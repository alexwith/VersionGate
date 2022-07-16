package net.versiongate.standalone.worker;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.versiongate.standalone.Server;

public class WorkerContext {
    private final ByteBuffer readBuffer = allocate(Server.SOCKET_BUFFER_SIZE);

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