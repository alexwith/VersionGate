package net.versiongate.common.translation.protocolstate.gate;

import net.versiongate.api.buffer.BufferType;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.protocolstate.type.handshaking.InboundPacketHandshaking;

public class HandshakePacketGate extends PacketGate {

    //Protocol Version: VarInt
    //Server Address: String (255)
    //Server Port: Unsigned Short
    //Next State: VarInt Enum

    @Override
    public void load() {
        this.packetConsumer(InboundPacketHandshaking.HANDSHAKE, (packet) -> {
            final int protocolVersion = packet.readWrite(BufferType.VAR_INT);
            packet.readWrite(BufferType.STRING);
            packet.readWrite(BufferType.UNSIGNED_SHORT);
            final int state = packet.readWrite(BufferType.VAR_INT);

            final IConnection connection = packet.getConnection();
            connection.setProtocolVersion(protocolVersion);
            connection.setProtocolState(ProtocolState.values()[state]);
        });
    }
}
