package net.versiongate.standalone.packet;

import java.nio.ByteBuffer;
import java.util.UUID;
import net.versiongate.standalone.util.ProtocolUtils;

public class MinecraftBuffer {
    private final ByteBuffer buffer;

    private MinecraftBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static MinecraftBuffer wrap(ByteBuffer buffer) {
        return new MinecraftBuffer(buffer);
    }

    public byte readByte() {
        return this.buffer.get();
    }

    public void writeByte(byte value) {
        this.buffer.put(value);
    }

    public int readVarInt() {
        return ProtocolUtils.readVarInt(this.buffer);
    }

    public void writeVarInt(int value) {
        ProtocolUtils.writeVarInt(this.buffer, value);
    }

    public String readString(int length) {
        return ProtocolUtils.readString(this.buffer, length);
    }

    public void writeString(String string) {
        ProtocolUtils.writeString(this.buffer, string);
    }

    public UUID readUuid() {
        return new UUID(this.buffer.getLong(), this.buffer.getLong());
    }

    public void writeUuid(UUID uuid) {
        this.buffer.putLong(uuid.getMostSignificantBits());
        this.buffer.putLong(uuid.getLeastSignificantBits());
    }
}