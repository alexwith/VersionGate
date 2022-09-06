package net.versiongate.api.buffer.adapter;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;

public class VarIntAdapter implements BufferAdapter<Integer> {

    @Override
    public Integer read(ByteBuf buffer) {
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
    }

    @Override
    public void write(ByteBuf buffer, Integer value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                buffer.writeByte(value);
                return;
            }

            buffer.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
    }
}
