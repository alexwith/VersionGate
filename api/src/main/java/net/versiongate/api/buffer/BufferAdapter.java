package net.versiongate.api.buffer;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.adapter.ByteBufAdapter;
import net.versiongate.api.buffer.adapter.JsonObjectAdapter;
import net.versiongate.api.buffer.adapter.LongArrayAdapter;
import net.versiongate.api.buffer.adapter.StringAdapter;
import net.versiongate.api.buffer.adapter.UUIDAdapter;
import net.versiongate.api.buffer.adapter.VarIntAdapter;
import net.versiongate.api.buffer.adapter.VarLongAdapter;

public interface BufferAdapter<T> {

    /**
     * This will read the type T from the buffer provided
     *
     * @param buffer The {@link ByteBuf} we will read from
     * @return The object that we read with the type T
     */
    T read(ByteBuf buffer);

    /**
     * This will write the type T to the buffer specified
     *
     * @param buffer The {@link ByteBuf} we will write to
     * @param value  The object that we will write to the buffer
     */
    void write(ByteBuf buffer, T value);

    ByteBufAdapter<Integer> INT = ByteBufAdapter.of(ByteBuf::readInt, ByteBuf::writeInt);
    ByteBufAdapter<Byte> BYTE = ByteBufAdapter.of(ByteBuf::readByte, (buffer, value) -> buffer.writeByte(value));
    ByteBufAdapter<Short> SHORT = ByteBufAdapter.of(ByteBuf::readShort, (buffer, value) -> buffer.writeShort(value));
    ByteBufAdapter<Float> FLOAT = ByteBufAdapter.of(ByteBuf::readFloat, ByteBuf::writeFloat);
    ByteBufAdapter<Double> DOUBLE = ByteBufAdapter.of(ByteBuf::readDouble, ByteBuf::writeDouble);
    ByteBufAdapter<Long> LONG = ByteBufAdapter.of(ByteBuf::readLong, ByteBuf::writeLong);

    ByteBufAdapter<Integer> UNSIGNED_SHORT = ByteBufAdapter.of(ByteBuf::readUnsignedShort, ByteBuf::writeShort);
    ByteBufAdapter<Short> UNSIGNED_BYTE = ByteBufAdapter.of(ByteBuf::readUnsignedByte, (buffer, value) -> buffer.writeByte(value));

    ByteBufAdapter<Boolean> BOOLEAN = ByteBufAdapter.of(ByteBuf::readBoolean, ByteBuf::writeBoolean);

    VarIntAdapter VAR_INT = new VarIntAdapter();
    VarLongAdapter VAR_LONG = new VarLongAdapter();

    LongArrayAdapter LONG_ARRAY = new LongArrayAdapter();

    StringAdapter STRING = new StringAdapter();
    UUIDAdapter UUID = new UUIDAdapter();
    JsonObjectAdapter JSON_OBJECT = new JsonObjectAdapter();
}