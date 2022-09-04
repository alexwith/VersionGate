package net.versiongate.common.gate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.IGateManager;
import net.versiongate.api.gate.IGateType;
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
    public Set<IGate> getHandshakingGates() {
        return this.handshakingGates;
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
    public void initialLoad() {
        for (final IGateType type : GateType.values()) {
            final IProtocolGate protocolGate = type.getProtocolGate();
            final Collection<? extends IGate> gates = this.loadGates(protocolGate.packetGates());

            protocolGate.load();

            if (type == GateType.PROTOCOL_STATE) {
                this.handshakingGates.addAll(gates);
            } else {
                this.versionGates.compute(type.getProtocolVersion(), ($, mapGates) -> {
                    if (mapGates == null) {
                        return UnifiedSet.newSet(gates);
                    }

                    mapGates.addAll(gates);
                    return mapGates;
                });
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends IGate> Collection<T> loadGates(T... gates) {
        return (Collection<T>) this.loadGates(Arrays.asList(gates));
    }

    private Collection<? extends IGate> loadGates(Collection<? extends IGate> gates) {
        for (final IGate gate : gates) {
            gate.load();
        }

        return gates;
    }
}
