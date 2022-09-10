package net.versiongate.common.translation.version1_8.gate;

import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.minecraft.chunk.Chunk;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.version1_8.buffer.BufferAdapter1_8;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;
import net.versiongate.common.translation.version1_9.type.OutboundPacket1_9;

public class WorldPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacket1_8.MAP_BULK_CHUNK, (packet) -> {
            packet.cancel();
        });

        this.packetConsumer(OutboundPacket1_8.CHUNK_DATA, (packet) -> {
            packet.schema(
                BufferAdapter1_8.CHUNK
            );

            final Chunk chunk = packet.getField(0);
            if (chunk.isFullChunk() && chunk.getBitmask() == 0) { // Unload chunk
                packet.setType(OutboundPacket1_9.UNLOAD_CHUNK);
                packet.schema(
                    BufferAdapter.INT,
                    BufferAdapter.INT
                );

                packet.setField(0, chunk.getX());
                packet.setField(1, chunk.getX());
                return;
            }

            
        });
    }
}
