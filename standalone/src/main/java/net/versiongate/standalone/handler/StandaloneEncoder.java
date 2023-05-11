package net.versiongate.standalone.handler;

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
    protected void encode(ChannelHandlerContext context, Object msg, ByteBuf out) {
        if (!(msg instanceof ByteBuf)) {
            context.write(msg);
            return;
        }

        out.writeBytes((ByteBuf) msg);

        this.connection.translate(out, PacketBound.OUT, true);
    }
}
