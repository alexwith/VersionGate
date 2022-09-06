package net.versiongate.common.translation.version1_8.gate;

import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;

public class WorldPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacket1_8.MAP_BULK_CHUNK, (packet) -> {
            packet.cancel();
            System.out.println("cancel map bulk chunk");
        });
    }
}
