package net.versiongate.standalone.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.versiongate.api.buffer.BufferAdapter;

public class FrameEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext context, ByteBuf packet, ByteBuf out) {
        if (packet.readableBytes() > 0) {
            BufferAdapter.VAR_INT.write(out, packet.readableBytes());
        }

        out.writeBytes(packet);
    }
}
