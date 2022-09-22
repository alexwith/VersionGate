package net.versiongate.common.translation.version1_8.gate;

import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;

public class EntityPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacket1_8.SPAWN_MOB, (packet) -> {
            packet.schema(
                BufferAdapter.VAR_INT, // EntityID
                BufferAdapter.UNSIGNED_SHORT, // Type
                BufferAdapter.INT, // X
                BufferAdapter.INT, // Y
                BufferAdapter.INT, // Z
                BufferAdapter.BYTE, // Yaw
                BufferAdapter.BYTE, // Pitch
                BufferAdapter.BYTE, // Head Pitch
                BufferAdapter.SHORT, // Velocity X
                BufferAdapter.SHORT, // Velocity Y
                BufferAdapter.SHORT // Velocity Z
            );

            packet.cancel();
        });

        this.packetConsumer(OutboundPacket1_8.ENTITY_METADATA, (packet) -> {
            packet.cancel();
        });

        this.packetConsumer(OutboundPacket1_8.ENTITY_PROPERTIES, (packet) -> {
            packet.cancel();
        });

        this.packetConsumer(OutboundPacket1_8.ENTITY_HEAD_LOOK, (packet) -> {
            packet.cancel();
        });

        this.packetConsumer(OutboundPacket1_8.ENTITY_VELOCITY, (packet) -> {
            packet.cancel();
        });

        this.packetConsumer(OutboundPacket1_8.ENTITY_TELEPORT, (packet) -> {
            packet.cancel();
        });

        this.packetConsumer(OutboundPacket1_8.BLOCK_ENTITY_DATA, (packet) -> {
            packet.cancel();
        });
    }
}
