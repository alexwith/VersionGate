package net.versiongate.common.translation.version1_8;

import java.util.Set;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.common.gate.gate.ProtocolGate;
import net.versiongate.common.translation.version1_8.gate.PlayerPacketsGate;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public class Version1_8 extends ProtocolGate {

    @Override
    public void load() {

    }

    @Override
    public Set<IPacketGate> packetGates() {
        return UnifiedSet.newSetWith(
            new PlayerPacketsGate()
        );
    }
}
