package net.versiongate.standalone.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.PacketBound;

public class StandaloneEncoder extends MessageToByteEncoder<Object> {
    private final IConnection connection;

    public StandaloneEncoder(IConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void encode(ChannelHandlerContext context, Object message, ByteBuf out) {
        if (!(message instanceof ByteBuf)) {
            context.write(message);
            return;
        }

        final ByteBuf packet = (ByteBuf) message;

        BufferAdapter.VAR_INT.read(packet); // read the prefixed length, the FrameEncoder adds this after

        final ByteBuf translationBuffer = context.alloc().buffer();
        translationBuffer.writeBytes(packet);

        this.connection.translate(translationBuffer, PacketBound.OUT);

        context.write(translationBuffer);
    }
}