package net.versiongate.common.connection;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.Arrays;
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
import net.versiongate.common.platform.Platform;

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
    public Channel getChannel() {
        return this.channel;
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
    public void translate(ByteBuf buffer, PacketBound bound, boolean lengthPrefixed) {
        if (!buffer.isReadable()) {
            return;
        }

        if (this.protocolVersion == Platform.get().getGateManager().getProtocolVersion()) {
            return;
        }

        final int packetLength = lengthPrefixed ? BufferAdapter.VAR_INT.read(buffer) : -1;

        final byte[] data = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), data);

        System.out.println(String.format("length: %d, dataLength: %d, data: %s", data.length, packetLength, Arrays.toString(data)));

        final int preReadingLength = buffer.readableBytes();
        final int packetId = BufferAdapter.VAR_INT.read(buffer);
        final IPacketType packetType = PacketTypes.getPacketType(GateType.VERSION1_8, this.protocolState, bound, packetId);

        System.out.println("packet: 0x" + Integer.toHexString(packetId) + " - " + packetType + " -> " + this.protocolState + " -> " + bound);

        if (packetType == null) {
            this.completeBuffer(buffer, (completedBuffer) -> {
                BufferAdapter.VAR_INT.write(completedBuffer, packetId);
                completedBuffer.writeBytes(buffer);
            }, packetLength, preReadingLength);
            return;
        }

        final IPacket packet = new Packet(this, packetType, buffer);
        this.connectionGate.translatePacket(packet);
        if (packet.isCancelled()) {
            return;
        }

        this.completeBuffer(buffer, packet::writeTo, packetLength, preReadingLength);
        System.out.println("Processed: " + packetType + " -> " + bound + ", 0x" + Integer.toHexString(packetId) + " -> " + buffer.writerIndex());

        if (packetType.getStateApplication() == ProtocolState.PLAY) {
            try {
                Thread.sleep(950);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean shouldTranslate() {
        return true;
    }

    private void completeBuffer(ByteBuf buffer, Consumer<ByteBuf> consumer, int packetLength, int preReadingLength) {
        final ByteBuf completedBuffer = buffer.alloc().buffer();
        try {
            consumer.accept(completedBuffer);
            buffer.clear();

            if (packetLength != -1) {
                final int postWriteLength = completedBuffer.readableBytes();
                final int lengthOffset = postWriteLength - preReadingLength;
                BufferAdapter.VAR_INT.write(buffer, packetLength + lengthOffset);
            }

            buffer.writeBytes(completedBuffer);
        } finally {
            completedBuffer.release();
        }
    }
}
