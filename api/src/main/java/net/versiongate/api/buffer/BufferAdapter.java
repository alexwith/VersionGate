package net.versiongate.api.buffer;

import io.netty.buffer.ByteBuf;

public interface BufferAdapter {

    /**
     * This will read the type T from the buffer provided
     *
     * @param buffer The {@link ByteBuf} we will read from
     * @return The object that we read with the type T
     */
    <T> T read(ByteBuf buffer);

    /**
     * This will write the type T to the buffer specified
     *
     * @param buffer The {@link ByteBuf} we will write to
     * @param value The object that we will write to the buffer
     */
    <T> void write(ByteBuf buffer, T value);
}