package net.versiongate.standalone.netty.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import net.versiongate.standalone.encryption.StandaloneCipher;

public class CipherDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final StandaloneCipher cipher;

    public CipherDecoder(StandaloneCipher cipher) {
        this.cipher = cipher;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf packet, List<Object> out) {
        CipherEndecProcessor.processPacket(this.cipher, context, packet, out);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.cipher.close();
    }
}
