package net.versiongate.api.buffer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public enum BufferType {

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
    STRING(
        (buffer) -> {
            final int length = VAR_INT.read(buffer);
            final String string = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
            buffer.skipBytes(length);

            return string;
        },
        (buffer, value) -> {
            final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            BufferType.VAR_INT.write(buffer, bytes.length);
            buffer.writeBytes(bytes);
        }
    ),
    JSON(
        (buffer) -> {
            final String jsonString = BufferType.STRING.read(buffer);
            return new Gson().fromJson(jsonString, JsonElement.class).getAsJsonObject();
        },
        (buffer, value) -> {
            BufferType.STRING.write(buffer, value.toString());
        }
    ),
    UNSIGNED_SHORT(
        ByteBuf::readUnsignedShort,
        ByteBuf::writeShort
    );

    private final Function<ByteBuf, Object> reader;
    private final BiConsumer<ByteBuf, Object> writer;

    <T> BufferType(Function<ByteBuf, T> reader, BiConsumer<ByteBuf, T> writer) {
        this.reader = (Function<ByteBuf, Object>) reader;
        this.writer = (BiConsumer<ByteBuf, Object>) writer;
    }

    public <T> T read(ByteBuf buffer) {
        return (T) this.reader.apply(buffer);
    }

    public <T> void write(ByteBuf buffer, T value) {
        this.writer.accept(buffer, value);
    }
}
