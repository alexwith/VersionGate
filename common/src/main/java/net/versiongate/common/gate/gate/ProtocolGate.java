package net.versiongate.common.gate.gate;

import java.util.Set;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.api.gate.gate.IProtocolGate;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public abstract class ProtocolGate implements IProtocolGate {
    private final Set<IPacketGate> packetGates = UnifiedSet.newSet();
}
