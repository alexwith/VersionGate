package net.versiongate.standalone.netty.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import net.versiongate.standalone.encryption.StandaloneCipher;

public class CipherEndecProcessor {

    public static void processPacket(StandaloneCipher cipher, ChannelHandlerContext context, ByteBuf packet, List<Object> out) {
        final ByteBuf compatible = ensureCompatible(context.alloc(), packet).slice();

        try {
            cipher.process(compatible);
            out.add(compatible);
        } catch (Exception e) {
            compatible.release();
            throw e;
        }
    }

    private static ByteBuf ensureCompatible(ByteBufAllocator alloc, ByteBuf buf) {
        if (buf.hasArray()) {
            return buf.retain();
        }

        final ByteBuf newBuf = alloc.directBuffer(buf.readableBytes());
        newBuf.writeBytes(buf);
        return newBuf;
    }
}
