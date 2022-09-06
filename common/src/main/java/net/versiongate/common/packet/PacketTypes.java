package net.versiongate.common.packet;

import java.util.Map;
import java.util.function.BiConsumer;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.api.gate.IGateType;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.gate.GateType;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

public class PacketTypes {
    private static final Map<ProtocolState, Map<IGateType, VersionedPackets>> PACKET_TYPES = UnifiedMap.newMap();

    static {
        computeTypes();
    }

    public static IPacketType getPacketType(IGateType gateType, ProtocolState state, PacketBound bound, int packetId) {
        final Map<Integer, IPacketType> types = PACKET_TYPES.get(state)
            .get(state != ProtocolState.PLAY ? GateType.PROTOCOL_STATE : gateType)
            .getPacketTypes(bound);

        return types.get(packetId);
    }

    private static void computeTypes() {
        final BiConsumer<GateType, IPacketType> populator = (gateType, type) -> {
            PACKET_TYPES.compute(type.getStateApplication(), ($, map) -> {
                if (map == null) {
                    map = UnifiedMap.newMap();
                }

                final VersionedPackets packets = map.computeIfAbsent(gateType, VersionedPackets::new);
                packets.getPacketTypes(type.getPacketBound()).put(type.getId(), type);
                return map;
            });
        };

        for (final GateType gateType : GateType.values()) {
            for (final IPacketType type : gateType.getInbound()) {
                populator.accept(gateType, type);
            }

            for (final IPacketType type : gateType.getOutbound()) {
                populator.accept(gateType, type);
            }
        }
    }

    private static class VersionedPackets {
        private final IGateType gateType;
        private final Map<Integer, IPacketType> inbound = UnifiedMap.newMap();
        private final Map<Integer, IPacketType> outbound = UnifiedMap.newMap();

        public VersionedPackets(IGateType gateType) {
            this.gateType = gateType;
        }

        public Map<Integer, IPacketType> getPacketTypes(PacketBound bound) {
            return bound == PacketBound.IN ? this.inbound : this.outbound;
        }
    }
}
