package net.versiongate.common.translation.version1_8.gate;

import net.versiongate.api.buffer.AdapterType;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;

public class EntityPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacket1_8.SPAWN_MOB, (packet) -> {
            packet.schema(
                AdapterType.VAR_INT, // EntityID
                AdapterType.UNSIGNED_SHORT, // Type
                AdapterType.INT, // X
                AdapterType.INT, // Y
                AdapterType.INT, // Z
                AdapterType.BYTE, // Yaw
                AdapterType.BYTE, // Pitch
                AdapterType.BYTE, // Head Pitch
                AdapterType.SHORT, // Velocity X
                AdapterType.SHORT, // Velocity Y
                AdapterType.SHORT // Velocity Z
            );

            packet.cancel(); // temp
        });
    }
}
