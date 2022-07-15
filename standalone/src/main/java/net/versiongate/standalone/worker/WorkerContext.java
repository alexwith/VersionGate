package net.versiongate.standalone.worker;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.versiongate.standalone.Server;

public class WorkerContext {
    private final ByteBuffer readBuffer = allocate(Server.SOCKET_BUFFER_SIZE);
    private final ByteBuffer writeBuffer = allocate(Server.SOCKET_BUFFER_SIZE);
    private final ByteBuffer contentBuffer = allocate(Server.MAX_PACKET_SIZE);
    private final ByteBuffer transformPayload = allocate(Server.MAX_PACKET_SIZE);
    private final ByteBuffer transform = allocate(Server.MAX_PACKET_SIZE);
    private final Deflater deflater = new Deflater();
    private final Inflater inflater = new Inflater();

    public ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

    public ByteBuffer getWriteBuffer() {
        return this.writeBuffer;
    }

    public ByteBuffer getContentBuffer() {
        return this.contentBuffer;
    }

    public ByteBuffer getTransformPayload() {
        return this.transformPayload;
    }

    public ByteBuffer getTransform() {
        return this.transform;
    }

    public Deflater getDeflater() {
        return this.deflater;
    }

    public Inflater getInflater() {
        return this.inflater;
    }

    public void clearBuffers() {
        this.readBuffer.clear();
        this.writeBuffer.clear();
        this.contentBuffer.clear();
        this.transformPayload.clear();
        this.transform.clear();
    }

    private static ByteBuffer allocate(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }
}