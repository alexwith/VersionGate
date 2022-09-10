package net.versiongate.api.buffer.adapter;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;

public class LongArrayAdapter implements BufferAdapter<long[]> {

    @Override
    public long[] read(ByteBuf buffer) {
        final int length = BufferAdapter.VAR_INT.read(buffer);
        final long[] array = new long[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = buffer.readLong();
        }

        return array;
    }

    @Override
    public void write(ByteBuf buffer, long[] array) {
        BufferAdapter.INT.write(buffer, array.length);
        for (final long value : array) {
            buffer.writeLong(value);
        }
    }
}
