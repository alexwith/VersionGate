package net.versiongate.standalone.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.PacketBound;

public class StandaloneDecoder extends ByteToMessageDecoder {
    private final IConnection connection;

    public StandaloneDecoder(IConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf packet, List<Object> out) {
        final boolean shouldTranslate = this.connection.shouldTranslate();
        if (!packet.isReadable() || !shouldTranslate) {
            context.fireChannelRead(packet);
            return;
        }

        this.connection.translate(packet, PacketBound.IN);

        final ByteBuf translationBuffer = context.alloc().buffer();
        BufferAdapter.VAR_INT.write(translationBuffer, packet.readableBytes());
        translationBuffer.writeBytes(packet);

        context.fireChannelRead(translationBuffer);
    }
}