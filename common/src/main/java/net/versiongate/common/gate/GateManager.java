package net.versiongate.common.gate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.IGateManager;
import net.versiongate.api.gate.gate.IProtocolGate;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.common.translation.protocolstate.gate.HandshakePacketGate;
import net.versiongate.common.translation.protocolstate.gate.LoginPacketGate;
import net.versiongate.common.translation.protocolstate.gate.StatusPacketGate;
import net.versiongate.common.translation.version1_8.Version1_8;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public class GateManager implements IGateManager {
    private final Set<IGate> handshakingGates = UnifiedSet.newSet();

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

        // This is temporary until we introduce more versions, this is just to get 1.8 -> 1.9 working
        final IProtocolGate protocolGate = new Version1_8();
        protocolGate.load();

        this.handshakingGates.addAll(this.loadGates(protocolGate.packetGates()));
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
