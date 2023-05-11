package net.versiongate.standalone.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.PacketBound;

public class StandaloneDecoder extends ByteToMessageDecoder {
    private final IConnection connection;

    public StandaloneDecoder(IConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf message, List<Object> out) throws Exception {
        final boolean shouldTranslate = this.connection.shouldTranslate();
        if (!message.isReadable() || !shouldTranslate) {
            context.fireChannelRead(message);
            return;
        }

        final ByteBuf translationBuffer = context.alloc().buffer();
        translationBuffer.writeBytes(message);

        this.connection.translate(translationBuffer, PacketBound.IN, true);

        context.fireChannelRead(translationBuffer);
    }
}

/* These are the expected packets for debugging
data: 16 -> [0, -44, 2, 9, 108, 111, 99, 97, 108, 104, 111, 115, 116, 99, -34, 1]
packet: 0x0 - HANDSHAKE -> HANDSHAKING -> IN

data: 1 -> [0]
packet: 0x0 - STATUS_REQUEST -> STATUS -> IN

data: 118 -> [0, 116, 123, 34, 100, 101, 115, 99, 114, 105, 112, 116, 105, 111, 110, 34, 58, 34, 65, 32, 77, 105, 110, 101, 99, 114, 97, 102, 116, 32, 83, 101, 114, 118, 101, 114, 34, 44, 34, 112, 108, 97, 121, 101, 114, 115, 34, 58, 123, 34, 109, 97, 120, 34, 58, 50, 48, 44, 34, 111, 110, 108, 105, 110, 101, 34, 58, 48, 125, 44, 34, 118, 101, 114, 115, 105, 111, 110, 34, 58, 123, 34, 110, 97, 109, 101, 34, 58, 34, 83, 112, 105, 103, 111, 116, 32, 49, 46, 56, 46, 56, 34, 44, 34, 112, 114, 111, 116, 111, 99, 111, 108, 34, 58, 52, 55, 125, 125]
packet: 0x0 - STATUS_RESPONSE -> STATUS -> OUT

data: 9 -> [1, 0, 0, 1, -120, 12, 53, 4, -4]
packet: 0x1 - PING_REQUEST -> STATUS -> IN

data: 9 -> [1, 0, 0, 1, -120, 12, 53, 4, -4]
packet: 0x1 - PING_RESPONSE -> STATUS -> OUT
 */