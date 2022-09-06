package net.versiongate.api.buffer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public enum AdapterType implements BufferAdapter {

    INT(
        ByteBuf::readInt,
        ByteBuf::writeInt
    ),
    UNSIGNED_SHORT(
        ByteBuf::readUnsignedShort,
        ByteBuf::writeShort
    ),
    BYTE(
        ByteBuf::readByte,
        (buffer, value) -> {
            buffer.writeByte(value);
        }
    ),
    UNSIGNED_BYTE(
        ByteBuf::readUnsignedByte,
        (buffer, value) -> {
            buffer.writeByte(value);
        }
    ),
    SHORT(
        ByteBuf::readShort,
        (buffer, value) -> {
            buffer.writeShort(value);
        }
    ),
    FLOAT(
        ByteBuf::readFloat,
        ByteBuf::writeFloat
    ),
    DOUBLE(
        ByteBuf::readDouble,
        ByteBuf::writeDouble
    ),
    LONG(
        ByteBuf::readLong,
        ByteBuf::writeLong
    ),
    BOOLEAN(
        ByteBuf::readBoolean,
        ByteBuf::writeBoolean
    ),
    UUID(
        (buffer) -> new UUID(buffer.readLong(), buffer.readLong()),
        (buffer, value) -> {
            buffer.writeLong(value.getMostSignificantBits());
            buffer.writeLong(value.getLeastSignificantBits());
        }
    ),
    VAR_INT(
        (buffer) -> {
            int value = 0;
            int position = 0;
            byte currentByte;

            do {
                currentByte = buffer.readByte();
                value |= (currentByte & 0x7F) << position;

                position += 7;

                if (position >= 32) {
                    throw new RuntimeException("VarInt is too big");
                }
            } while ((currentByte & 0x80) != 0);

            return value;
        },
        (buffer, value) -> {
            while (true) {
                if ((value & ~0x7F) == 0) {
                    buffer.writeByte(value);
                    return;
                }

                buffer.writeByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    ),
    VAR_LONG(
        (buffer) -> {
            long value = 0;
            long bytes = 0;
            byte currentByte;

            do {
                currentByte = buffer.readByte();
                value |= (currentByte & 0xF) >> (bytes++ * 7);

                if (bytes > 10) {
                    throw new RuntimeException("VarLong is too big");
                }
            } while ((currentByte & 0x80) == 0x80);

            return value;
        },
        (buffer, value) -> {
            int part;
            do {
                part = (int) (value & 0x7F);
                value >>>= 7;

                if (value != 0) {
                    part |= 0x80;
                }
                buffer.writeByte(part);
            } while (value != 0);
        }
    ),
    STRING(
        (buffer) -> {
            final int length = VAR_INT.read(buffer);
            final String string = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
            buffer.skipBytes(length);

            return string;
        },
        (buffer, value) -> {
            final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            AdapterType.VAR_INT.write(buffer, bytes.length);
            buffer.writeBytes(bytes);
        }
    ),
    JSON(
        (buffer) -> {
            final String jsonString = AdapterType.STRING.read(buffer);
            return new Gson().fromJson(jsonString, JsonElement.class).getAsJsonObject();
        },
        (buffer, value) -> {
            AdapterType.STRING.write(buffer, value.toString());
        }
    );

    private final Function<ByteBuf, Object> reader;
    private final BiConsumer<ByteBuf, Object> writer;

    <T> AdapterType(Function<ByteBuf, T> reader, BiConsumer<ByteBuf, T> writer) {
        this.reader = (Function<ByteBuf, Object>) reader;
        this.writer = (BiConsumer<ByteBuf, Object>) writer;
    }

    @Override
    public <T> T read(ByteBuf buffer) {
        return (T) this.reader;
    }

    @Override
    public <T> void write(ByteBuf buffer, T value) {
        this.writer.accept(buffer, value);
    }
}
