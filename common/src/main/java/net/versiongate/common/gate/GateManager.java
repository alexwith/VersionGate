package net.versiongate.common.gate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.IGateManager;
import net.versiongate.api.gate.IGateType;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.api.gate.gate.IProtocolGate;
import net.versiongate.api.gate.version.ProtocolVersion;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public class GateManager implements IGateManager {
    private final Set<IGate> handshakingGates = UnifiedSet.newSet();
    private final Map<ProtocolVersion, Set<IGate>> versionGates = UnifiedMap.newMap();

    private ProtocolVersion protocolVersion = ProtocolVersion.UNKNOWN;

    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public void setProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public boolean hasProtocolVersion() {
        return this.protocolVersion != ProtocolVersion.UNKNOWN;
    }

    @Override
    public Set<IGate> getHandshakingGates() {
        return this.handshakingGates;
    }

    @Override
    public Set<IGate> getVersionGates(ProtocolVersion protocolVersion) {
        return this.versionGates.get(protocolVersion);
    }

    @Override
    public void initialLoad() {
        for (final IGateType gateType : GateType.values()) {
            gateType.mapPacketTypes();

            final IProtocolGate protocolGate = gateType.getProtocolGate();
            final Collection<? extends IGate> gates = this.loadPacketGates(gateType, protocolGate.packetGates());

            protocolGate.load();

            if (gateType == GateType.PROTOCOL_STATE) {
                this.handshakingGates.addAll(gates);
                continue;
            }

            this.versionGates.compute(gateType.getProtocolVersion(), ($, mapGates) -> {
                if (mapGates == null) {
                    return UnifiedSet.newSet(gates);
                }

                mapGates.addAll(gates);
                return mapGates;
            });
        }
    }

    private Collection<? extends IGate> loadPacketGates(IGateType gateType, Collection<IPacketGate> gates) {
        for (final IPacketGate gate : gates) {
            gate.setGateType(gateType);
            gate.load();
        }

        return gates;
    }
}
