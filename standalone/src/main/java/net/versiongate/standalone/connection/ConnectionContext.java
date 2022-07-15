package net.versiongate.standalone.connection;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import net.versiongate.standalone.enums.PacketBound;
import net.versiongate.standalone.enums.State;
import net.versiongate.standalone.util.ProtocolUtils;
import net.versiongate.standalone.worker.WorkerContext;

public class ConnectionContext {
    private final SocketChannel target;
    private final ProtocolHandler handler;
    private final PacketBound packetBound;
    private final ProtocolFormat protocolFormat = ProtocolFormat.VANILLA;

    private State state = State.HANDSHAKE;
    private ByteBuffer cachedBuffer;
    private ConnectionContext targetConnectionContext;
    private boolean compression;
    private int compressionThreshold;

    public ConnectionContext(SocketChannel target, ProtocolHandler handler, PacketBound packetBound) {
        this.target = target;
        this.handler = handler;
        this.packetBound = packetBound;
    }

    public SocketChannel getTarget() {
        return this.target;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public ConnectionContext getTargetContext() {
        return this.targetConnectionContext;
    }

    public void setTargetContext(ConnectionContext targetConnectionContext) {
        this.targetConnectionContext = targetConnectionContext;
    }

    public boolean isCompression() {
        return this.compression;
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    public void setCompression(int threshold) {
        this.compression = threshold > 0;
        this.compressionThreshold = threshold;
    }

    public void applyCache(ByteBuffer buffer) {
        if (this.cachedBuffer == null) {
            return;
        }
        buffer.put(this.cachedBuffer);
        this.cachedBuffer = null;
    }

    public void processPackets(WorkerContext context) {
        final ByteBuffer readBuffer = context.getReadBuffer();
        final int limit = readBuffer.limit();

        while (readBuffer.remaining() > 0) {
            readBuffer.mark();
            try {
                final int packetLength = ProtocolUtils.readVarInt(readBuffer);
                final int packetEnd = readBuffer.position() + packetLength;
                if (packetEnd > readBuffer.limit()) {
                    throw new BufferUnderflowException();
                }

                readBuffer.limit(packetEnd);

                var content = context.getContentBuffer().clear();
                if (this.protocolFormat.read(this, readBuffer, content, context)) {
                    content = readBuffer;
                } else {
                    content.flip();
                }

                if (!this.writeContent(readBuffer, content, context)) {
                    break;
                }

                final int packetId = ProtocolUtils.readVarInt(content);
                try {
                    this.handler.process(this, packetId, content);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

                readBuffer.limit(limit).position(packetEnd);
            } catch (BufferUnderflowException e) {
                readBuffer.reset();
                this.cachedBuffer = ByteBuffer.allocateDirect(readBuffer.remaining());
                this.cachedBuffer.put(readBuffer).flip();
                break;
            }
        }

        try {
            this.write(context.getWriteBuffer().flip());
        } catch (IOException e) {
            // client disconnect
        }
    }

    private boolean writeContent(ByteBuffer readBuffer, ByteBuffer content, WorkerContext workerContext) {
        final int contentPositionCache = content.position();
        final ByteBuffer writeCache = readBuffer.reset();
        final boolean result = this.incrementalWrite(writeCache, workerContext);
        content.position(contentPositionCache);
        return result;
    }

    private boolean incrementalWrite(ByteBuffer buffer, WorkerContext workerContext) {
        try {
            final ByteBuffer writeBuffer = workerContext.getWriteBuffer();
            try {
                writeBuffer.put(buffer);
            } catch (BufferOverflowException e) {
                this.write(writeBuffer.flip());
                this.write(buffer);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void write(ByteBuffer buffer) throws IOException {
        while (buffer.remaining() > 0) {
            this.target.write(buffer);
        }
    }
}
