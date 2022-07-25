package net.versiongate.common.gate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.IGateManager;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.common.translation.protocolstate.gate.HandshakePacketGate;
import net.versiongate.common.translation.protocolstate.gate.LoginPacketGate;
import net.versiongate.common.translation.protocolstate.gate.StatusPacketGate;

public class GateManager implements IGateManager {
    private final Set<IGate> handshakingGates = new HashSet<>();

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
        this.handshakingGates.addAll(this.loadGates(
            new HandshakePacketGate(),
            new LoginPacketGate(),
            new StatusPacketGate()
        ));
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
