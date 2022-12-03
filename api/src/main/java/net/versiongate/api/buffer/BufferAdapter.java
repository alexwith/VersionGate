package net.versiongate.api.buffer;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.adapter.ByteArrayAdapter;
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

    /**
     * The expected output type of the buffer adapter
     *
     * @return The expected output
     */
    Class<T> outputType();

    /**
     * This should be overridden where we want values to be transformed
     *
     * @param object The object to transform to type T
     * @return The transformed object
     */
    @SuppressWarnings("unchecked")
    default T transform(Object object) {
        return (T) object;
    }

    ByteBufAdapter<Integer> INT = ByteBufAdapter.of(ByteBuf::readInt, ByteBuf::writeInt, Integer.class, (object) -> {
        return object instanceof Number ? ((Number) object).intValue() : (Integer) object;
    });
    ByteBufAdapter<Byte> BYTE = ByteBufAdapter.of(ByteBuf::readByte, (buffer, value) -> buffer.writeByte(value), Byte.class);
    ByteBufAdapter<Short> SHORT = ByteBufAdapter.of(ByteBuf::readShort, (buffer, value) -> buffer.writeShort(value), Short.class);
    ByteBufAdapter<Float> FLOAT = ByteBufAdapter.of(ByteBuf::readFloat, ByteBuf::writeFloat, Float.class);
    ByteBufAdapter<Double> DOUBLE = ByteBufAdapter.of(ByteBuf::readDouble, ByteBuf::writeDouble, Double.class);
    ByteBufAdapter<Long> LONG = ByteBufAdapter.of(ByteBuf::readLong, ByteBuf::writeLong, Long.class);

    ByteBufAdapter<Integer> UNSIGNED_SHORT = ByteBufAdapter.of(ByteBuf::readUnsignedShort, ByteBuf::writeShort, Integer.class);
    ByteBufAdapter<Short> UNSIGNED_BYTE = ByteBufAdapter.of(ByteBuf::readUnsignedByte, (buffer, value) -> buffer.writeByte(value), Short.class);

    ByteBufAdapter<Boolean> BOOLEAN = ByteBufAdapter.of(ByteBuf::readBoolean, ByteBuf::writeBoolean, Boolean.class);

    VarIntAdapter VAR_INT = new VarIntAdapter();
    VarLongAdapter VAR_LONG = new VarLongAdapter();

    LongArrayAdapter LONG_ARRAY = new LongArrayAdapter();
    ByteArrayAdapter BYTE_ARRAY = new ByteArrayAdapter();

    StringAdapter STRING = new StringAdapter();
    UUIDAdapter UUID = new UUIDAdapter();
    JsonObjectAdapter JSON_OBJECT = new JsonObjectAdapter();
}