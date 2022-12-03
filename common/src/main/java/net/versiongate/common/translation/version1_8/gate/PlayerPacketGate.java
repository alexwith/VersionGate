package net.versiongate.common.translation.version1_8.gate;

import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;

public class PlayerPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacket1_8.PLAYER_POSITION, (packet) -> {
            packet.schema(
                BufferAdapter.DOUBLE, // X
                BufferAdapter.DOUBLE, // Y
                BufferAdapter.DOUBLE, // Z
                BufferAdapter.FLOAT, // Yaw
                BufferAdapter.FLOAT, // Pitch
                BufferAdapter.BYTE, // Flags
                BufferAdapter.VAR_INT // Teleport ID (new field)
            );

            packet.writeField(6, 0);
        });

        this.packetConsumer(OutboundPacket1_8.JOIN_GAME, (packet) -> {
            packet.schema(
                BufferAdapter.INT, // Entity ID
                BufferAdapter.UNSIGNED_BYTE, // Gamemode
                BufferAdapter.BYTE // Dimension (changed to int)
            );

            packet.setFieldAdapter(2, BufferAdapter.INT);
        });
    }
}