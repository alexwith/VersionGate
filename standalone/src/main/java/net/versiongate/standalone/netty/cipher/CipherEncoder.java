package net.versiongate.standalone.netty.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import net.versiongate.standalone.encryption.StandaloneCipher;
import net.versiongate.standalone.util.ChannelUtil;

public class CipherEncoder extends MessageToMessageEncoder<ByteBuf> {
    private final StandaloneCipher cipher;

    public CipherEncoder(StandaloneCipher cipher) {
        this.cipher = cipher;
    }

    @Override
    protected void encode(ChannelHandlerContext context, ByteBuf packet, List<Object> out) {
        CipherEndecProcessor.processPacket(this.cipher, context, packet, out);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.cipher.close();
    }
}
