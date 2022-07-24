package net.versiongate.common.gate;

import java.util.Collection;
import java.util.Set;
import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.IGateManager;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.protocolstate.gate.HandshakePacketGate;
import net.versiongate.common.translation.protocolstate.gate.LoginPacketGate;
import net.versiongate.common.translation.protocolstate.gate.StatusPacketGate;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public class GateManager implements IGateManager {

    public static final Set<PacketGate> HANDSHAKING_GATES = UnifiedSet.newSetWith(
        new HandshakePacketGate(),
        new LoginPacketGate(),
        new StatusPacketGate()
    );

    public GateManager() {
        this.loadGates(HANDSHAKING_GATES);
    }

    private <T extends IGate> void loadGate(T gate) {
        gate.load();
    }

    private void loadGates(Collection<? extends IGate> gates) {
        for (final IGate gate : gates) {
            this.loadGate(gate);
        }
    }
}
