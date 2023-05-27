package net.versiongate.api.buffer.adapter;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;

public class ByteArrayAdapter implements BufferAdapter<byte[]> {

    @Override
    public byte[] read(ByteBuf buffer) {
        final int length = BufferAdapter.VAR_INT.read(buffer);
        final byte[] array = new byte[length];
        buffer.readBytes(array);

        return array;
    }

    @Override
    public void write(ByteBuf buffer, byte[] value) {
        BufferAdapter.VAR_INT.write(buffer, value.length);
        buffer.writeBytes(value);
    }

    @Override
    public Class<byte[]> outputType() {
        return byte[].class;
    }
}
