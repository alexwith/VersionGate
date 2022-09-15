package net.versiongate.common.translation.version1_8.gate;

import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.minecraft.chunk.IChunk;
import net.versiongate.api.packet.IPacket;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.version1_8.buffer.BufferAdapter1_8;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;
import net.versiongate.common.translation.version1_9.buffer.BufferAdapter1_9;
import net.versiongate.common.translation.version1_9.type.OutboundPacket1_9;

public class WorldPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacket1_8.MAP_BULK_CHUNK, (packet) -> {
            packet.schema(
                BufferAdapter1_8.BULK_CHUNK
            );
            packet.cancel();

            final IChunk[] chunks = packet.getField(0);
            for (final IChunk chunk : chunks) {
                final IPacket chunkPacket = this.createPacket(packet.getConnection(), OutboundPacket1_9.CHUNK_DATA);
                chunkPacket.schema(
                    BufferAdapter1_9.CHUNK
                );

                chunkPacket.setField(0, chunk);
                chunkPacket.send();
            }
        });

        this.packetConsumer(OutboundPacket1_8.CHUNK_DATA, (packet) -> {
            packet.schema(
                BufferAdapter1_8.CHUNK
            );

            final IChunk chunk = packet.getField(0);
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

            packet.schema(
                BufferAdapter1_9.CHUNK
            );

            packet.setField(0, chunk);
        });
    }
}
