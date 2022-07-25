package net.versiongate.common.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.function.Consumer;
import net.versiongate.api.buffer.BufferType;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.connection.IConnectionGate;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.packet.Packet;
import net.versiongate.common.packet.PacketTypes;

public class Connection implements IConnection {
    private final Channel channel;
    private final IConnectionGate connectionGate;

    private ProtocolVersion protocolVersion;
    private ProtocolState protocolState = ProtocolState.HANDSHAKING;

    public Connection(Channel channel) {
        this.channel = channel;
        this.connectionGate = new ConnectionGate();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = ProtocolVersion.getClosest(protocolVersion);
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

        final int packetId = BufferType.VAR_INT.read(buffer);
        final IPacketType packetType = PacketTypes.getPacketType(this.protocolState, bound, packetId);
        System.out.println("packet sent: " + packetType);
        if (packetType == null) {
            this.completeBuffer(buffer, (completedBuffer) -> {
                BufferType.VAR_INT.write(completedBuffer, packetId);
                completedBuffer.writeBytes(buffer);
            });
            return;
        }

        final IPacket packet = new Packet(this, packetType, buffer);
        this.connectionGate.translatePacket(packet);
        if (packet.isCancelled()) {
            return;
        }

        this.completeBuffer(buffer, packet::writeTo);
    }

    @Override
    public boolean shouldTranslate() {
        return true;
    }

    private void completeBuffer(ByteBuf buffer, Consumer<ByteBuf> consumer) {
        final ByteBuf completedBuffer = buffer.alloc().buffer();
        try {
            consumer.accept(completedBuffer);
            buffer.clear().writeBytes(completedBuffer);
        } finally {
            completedBuffer.release();
        }
    }
}
