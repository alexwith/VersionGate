package net.versiongate.common.packet;

import java.util.Map;
import java.util.function.BiConsumer;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.gate.GateType;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

public class PacketTypes {
    private static final Map<ProtocolState, Map<Integer, IPacketType>> INBOUND = UnifiedMap.newMap();
    private static final Map<ProtocolState, Map<Integer, IPacketType>> OUTBOUND = UnifiedMap.newMap();

    static {
        computeTypes();
    }

    public static IPacketType getPacketType(ProtocolState state, PacketBound bound, int packetId) {
        final Map<Integer, IPacketType> types = (bound == PacketBound.IN ? INBOUND : OUTBOUND).get(state);

        return types.get(packetId);
    }

    private static void computeTypes() {
        final BiConsumer<Map<ProtocolState, Map<Integer, IPacketType>>, IPacketType> populator = (types, type) -> {
            types.compute(type.getStateApplication(), ($, map) -> {
                if (map == null) {
                    map = UnifiedMap.newMap();
                }

                map.put(type.getId(), type);
                return map;
            });
        };

        for (final GateType gateType : GateType.values()) {
            for (final IPacketType type : gateType.getInbound()) {
                populator.accept(INBOUND, type);
            }

            for (final IPacketType type : gateType.getOutbound()) {
                populator.accept(OUTBOUND, type);
            }
        }
    }
}
