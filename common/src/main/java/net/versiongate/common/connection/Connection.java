package net.versiongate.common.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.api.packet.IPacket;
import net.versiongate.common.packet.Packet;
import net.versiongate.common.util.ProtocolUtil;

public class Connection implements IConnection {
    private final Channel channel;

    private ProtocolState protocolState = ProtocolState.HANDSHAKING;

    public Connection(Channel channel) {
        this.channel = channel;
    }

    @Override
    public ProtocolState getProtocolState() {
        return this.protocolState;
    }

    @Override
    public void setProtocolState(ProtocolState protocolState) {
        this.protocolState = protocolState;
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

        final int packetId = ProtocolUtil.readVarInt(buffer);
        final IPacket packet = new Packet(packetId, buffer);

        // TODO this is where we translate

        final ByteBuf completeBuffer = buffer.alloc().buffer();
        try {
            packet.writeTo(completeBuffer);
            buffer.clear().writeBytes(completeBuffer);
        } finally {
            completeBuffer.release();
        }
    }

    @Override
    public boolean shouldTranslate() {
        return true;
    }
}
