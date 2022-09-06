package net.versiongate.api.buffer.adapter;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import net.versiongate.api.buffer.BufferAdapter;

public class StringAdapter implements BufferAdapter<String> {

    @Override
    public String read(ByteBuf buffer) {
        final int length = VAR_INT.read(buffer);
        final String string = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
        buffer.skipBytes(length);

        return string;
    }

    @Override
    public void write(ByteBuf buffer, String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        BufferAdapter.VAR_INT.write(buffer, bytes.length);
        buffer.writeBytes(bytes);
    }
}
