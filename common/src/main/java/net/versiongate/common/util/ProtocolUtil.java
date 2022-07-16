package net.versiongate.common.util;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class ProtocolUtil {

    public static String toProtocolHex(int number) {
        return String.format("0x%02X", number);
    }

    public static int readVarInt(ByteBuf buffer) {
        // Code from https://github.com/bazelbuild/bazel/blob/master/src/main/java/com/google/devtools/build/lib/util/VarInt.java
        int tmp;
        if ((tmp = buffer.readByte()) >= 0) {
            return tmp;
        }
        int result = tmp & 0x7f;
        if ((tmp = buffer.readByte()) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7f) << 7;
            if ((tmp = buffer.readByte()) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7f) << 14;
                if ((tmp = buffer.readByte()) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7f) << 21;
                    result |= (tmp = buffer.readByte()) << 28;
                    while (tmp < 0) {
                        // We get into this loop only in the case of overflow.
                        // By doing this, we can call getVarInt() instead of
                        // getVarLong() when we only need an int.
                        tmp = buffer.readByte();
                    }
                }
            }
        }
        return result;
    }

    public static String readString(ByteBuf buffer, int maxLength) {
        final int length = readVarInt(buffer);
        if (length > maxLength) {
            throw new IllegalArgumentException("String too long: " + length);
        }
        final byte[] data = new byte[length];
        buffer.readBytes(data);
        return new String(data, StandardCharsets.UTF_8);
    }

    public static void writeString(ByteBuf buffer, String string) {
        final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        writeVarInt(buffer, bytes.length);
        buffer.writeBytes(bytes);
    }

    public static void writeVarInt(ByteBuf buffer, int value) {
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buffer.writeByte((byte) value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            buffer.writeShort((short) ((value & 0x7F | 0x80) << 8 | (value >>> 7)));
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            buffer.writeByte((byte) (value & 0x7F | 0x80));
            buffer.writeByte((byte) ((value >>> 7) & 0x7F | 0x80));
            buffer.writeByte((byte) (value >>> 14));
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            buffer.writeInt((value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
                            | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21));
        } else {
            buffer.writeInt((value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
                            | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80));
            buffer.writeByte((byte) (value >>> 28));
        }
    }

    public static int writeEmptyVarIntHeader(ByteBuf buffer) {
        final int index = buffer.readerIndex();
        buffer.writeShort((short) 0);
        buffer.writeByte((byte) 0);
        return index;
    }

    public static void writeVarIntHeader(ByteBuf buffer, int startIndex, int value) {
        final int indexCache = buffer.readerIndex();
        buffer.readerIndex(startIndex);
        buffer.writeByte((byte) (value & 0x7F | 0x80));
        buffer.writeByte((byte) ((value >>> 7) & 0x7F | 0x80));
        buffer.writeByte((byte) (value >>> 14));
        buffer.readerIndex(indexCache);
    }

    public static void writeVarShort(ByteBuf buffer, int value) {
        final int high = (value & 0x7F8000) >> 15;

        int low = value & 0x7FFF;
        if (high != 0) {
            low = low | 0x8000;
        }

        buffer.writeShort(low);

        if (high != 0) {
            buffer.writeByte(high);
        }
    }
}
