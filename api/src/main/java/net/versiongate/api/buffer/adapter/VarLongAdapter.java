package net.versiongate.api.buffer.adapter;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;

public class VarLongAdapter implements BufferAdapter<Long> {

    @Override
    public Long read(ByteBuf buffer) {
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
    }

    @Override
    public void write(ByteBuf buffer, Long value) {
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
}
