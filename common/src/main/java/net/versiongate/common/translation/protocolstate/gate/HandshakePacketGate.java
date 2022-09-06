package net.versiongate.common.translation.protocolstate.gate;

import net.versiongate.api.buffer.AdapterType;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.protocolstate.type.handshaking.InboundPacketHandshaking;

public class HandshakePacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(InboundPacketHandshaking.HANDSHAKE, (packet) -> {
            packet.schema(
                AdapterType.VAR_INT,
                AdapterType.STRING,
                AdapterType.UNSIGNED_SHORT,
                AdapterType.VAR_INT
            );

            final int connectionProtocol = packet.getField(0);
            final int state = packet.getField(3);

            final IConnection connection = packet.getConnection();
            connection.setProtocolVersion(connectionProtocol);
            connection.setProtocolState(ProtocolState.values()[state]);

            final int serverProtocol = this.gateManager.getProtocolVersion().getId();
            packet.setField(0, serverProtocol);
        });
    }
}
