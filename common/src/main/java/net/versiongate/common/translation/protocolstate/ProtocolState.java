package net.versiongate.common.translation.protocolstate;

import java.util.Set;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.common.gate.gate.ProtocolGate;
import net.versiongate.common.translation.protocolstate.gate.HandshakePacketGate;
import net.versiongate.common.translation.protocolstate.gate.LoginPacketGate;
import net.versiongate.common.translation.protocolstate.gate.StatusPacketGate;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public class ProtocolState extends ProtocolGate {

    @Override
    public void load() {

    }

    @Override
    public Set<IPacketGate> packetGates() {
        return UnifiedSet.newSetWith(
            new HandshakePacketGate(),
            new LoginPacketGate(),
            new StatusPacketGate()
        );
    }
}
