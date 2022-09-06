package net.versiongate.common.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.function.Consumer;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.connection.IConnectionGate;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.gate.GateType;
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
        this.connectionGate.onSetProtocolVersion(this.protocolVersion);
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

        final int packetId = BufferAdapter.VAR_INT.read(buffer);
        final IPacketType packetType = PacketTypes.getPacketType(GateType.VERSION1_8, this.protocolState, bound, packetId);
        if (packetType == null) {
            this.completeBuffer(buffer, (completedBuffer) -> {
                BufferAdapter.VAR_INT.write(completedBuffer, packetId);
                completedBuffer.writeBytes(buffer);
            });
            return;
        }

        final IPacket packet = new Packet(this, packetType, buffer);
        this.connectionGate.translatePacket(packet);
        if (packet.isCancelled()) {
            System.out.println("cancelled packet: " + packet);
            return;
        }

        this.completeBuffer(buffer, packet::writeTo);

        if (packetType.getStateApplication() == ProtocolState.PLAY) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Processed: " + packetType + " -> " + bound);
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
