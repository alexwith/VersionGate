package net.versiongate.common.translation.version1_8.gate;

import net.versiongate.api.buffer.BufferType;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;

public class EntityPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacket1_8.SPAWN_MOB, (packet) -> {
            packet.schema(
                BufferType.VAR_INT, // EntityID
                BufferType.UNSIGNED_SHORT, // Type
                BufferType.INT, // X
                BufferType.INT, // Y
                BufferType.INT, // Z
                BufferType.BYTE, // Yaw
                BufferType.BYTE, // Pitch
                BufferType.BYTE, // Head Pitch
                BufferType.SHORT, // Velocity X
                BufferType.SHORT, // Velocity Y
                BufferType.SHORT // Velocity Z
            );
        });
    }
}
