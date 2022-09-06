package net.versiongate.api.buffer.adapter;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.versiongate.api.buffer.BufferAdapter;

public class UUIDAdapter implements BufferAdapter<UUID> {

    @Override
    public UUID read(ByteBuf buffer) {
        return new UUID(buffer.readLong(), buffer.readLong());
    }

    @Override
    public void write(ByteBuf buffer, UUID value) {
        buffer.writeLong(value.getMostSignificantBits());
        buffer.writeLong(value.getLeastSignificantBits());
    }
}
