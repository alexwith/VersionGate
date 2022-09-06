package net.versiongate.common.translation.version1_8.gate;

import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;

public class PlayerPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacket1_8.PLAYER_INFO, (packet) -> {
            packet.schema(
                BufferAdapter.VAR_INT,
                BufferAdapter.VAR_INT
            );

            final int action = packet.getField(0);
            final int playerCount = packet.getField(1);
        });
    }
}