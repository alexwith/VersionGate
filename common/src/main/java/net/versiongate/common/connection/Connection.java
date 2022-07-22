package net.versiongate.common.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.packet.IPacket;
import net.versiongate.common.packet.Packet;
import net.versiongate.common.util.ProtocolUtil;

public class Connection implements IConnection {
    private final Channel channel;

    public Connection(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void disconnect() {
        this.channel.close();
    }

    @Override
    public void translate(ByteBuf buffer, PacketBound bound) {
        if (!buffer.isReadable()) {
            return;
        }

        final int packetLength = 0;//ProtocolUtil.readVarInt(buffer);
        final int packetId = 0;//ProtocolUtil.readVarInt(buffer);
        final IPacket packet = new Packet(packetId, packetLength, buffer);
        System.out.println(ProtocolUtil.toProtocolHex(packetId) + " (" + packetId + ") Length: " + packetLength + " Bound: " + bound);

        // TODO this is where we translate

        final ByteBuf completeBuffer = buffer.alloc().buffer();
        try {
            packet.writeTo(completeBuffer);
            buffer.clear().writeBytes(completeBuffer);
        } finally {
            completeBuffer.release();
        }
    }
}
