package net.versiongate.common.util;

import io.netty.buffer.ByteBuf;

public class ProtocolUtil {

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public static String toProtocolHex(int number) {
        return String.format("0x%02X", number);
    }

    public static int readVarInt(ByteBuf buffer) {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = buffer.readByte();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) {
                break;
            }

            position += 7;

            if (position >= 32) {
                throw new RuntimeException("VarInt is too big");
            }
        }

        return value;
    }

    public static void writeVarInt(ByteBuf buffer, int value) {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                buffer.writeByte(value);
                return;
            }

            buffer.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
            value >>>= 7;
        }
    }
}
