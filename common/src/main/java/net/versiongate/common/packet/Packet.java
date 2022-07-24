package net.versiongate.common.packet;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferType;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;

public class Packet implements IPacket {
    private final IConnection connection;
    private final IPacketType type;
    private final ByteBuf contentBuffer;

    private boolean isCancelled;

    public Packet(IConnection connection, IPacketType type, ByteBuf contents) {
        this.connection = connection;
        this.type = type;
        this.contentBuffer = contents;
    }

    @Override
    public IConnection getConnection() {
        return this.connection;
    }

    @Override
    public IPacketType getType() {
        return this.type;
    }

    @Override
    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void writeTo(ByteBuf buffer) {
        if (this.contentBuffer == null) {
            return;
        }

        BufferType.VAR_INT.write(buffer, this.type.getId());
        buffer.writeBytes(this.contentBuffer);
    }

    @Override
    public <T> T readWrite(BufferType type) {
        final T value = this.read(type);
        this.write(type, value);

        return value;
    }

    @Override
    public <T> T read(BufferType type) {
        return type.read(this.contentBuffer);
    }

    @Override
    public <T> void write(BufferType type, T value) {
        type.write(this.contentBuffer, value);
    }
}
