package net.versiongate.common.translation.protocolstate.gate;

import net.versiongate.api.connection.IConnection;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.protocolstate.type.status.OutboundPacketStatus;

public class StatusPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacketStatus.STATUS_RESPONSE, (packet) -> {
            final IConnection connection = packet.getConnection();

        });
    }
}
