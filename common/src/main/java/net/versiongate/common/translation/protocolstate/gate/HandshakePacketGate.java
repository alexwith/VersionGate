package net.versiongate.common.translation.protocolstate.gate;

import net.versiongate.api.buffer.BufferType;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.protocolstate.type.handshaking.InboundPacketHandshaking;

public class HandshakePacketGate extends PacketGate {
    
    @Override
    public void load() {
        this.packetConsumer(InboundPacketHandshaking.HANDSHAKE, (packet) -> {
            packet.schema(
                BufferType.VAR_INT,
                BufferType.STRING,
                BufferType.UNSIGNED_SHORT,
                BufferType.VAR_INT
            );

            final int protocolVersion = packet.getField(0);
            final int state = packet.getField(3);

            final IConnection connection = packet.getConnection();
            connection.setProtocolVersion(protocolVersion);
            connection.setProtocolState(ProtocolState.values()[state]);
        });
    }
}
