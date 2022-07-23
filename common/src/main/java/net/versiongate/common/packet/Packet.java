package net.versiongate.common.packet;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferType;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.packet.IPacket;

public class Packet implements IPacket {
    private final IConnection connection;
    private final int id;
    private final ByteBuf contentBuffer;

    public Packet(IConnection connection, int id, ByteBuf contents) {
        this.connection = connection;
        this.id = id;
        this.contentBuffer = contents;
    }

    @Override
    public IConnection getConnection() {
        return this.connection;
    }

    @Override
    public void writeTo(ByteBuf buffer) {
        BufferType.VAR_INT.write(buffer, this.id);

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
