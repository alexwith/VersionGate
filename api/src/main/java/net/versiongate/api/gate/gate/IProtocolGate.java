package net.versiongate.api.gate.gate;

import java.util.Set;
import net.versiongate.api.gate.IGate;

public interface IProtocolGate extends IGate {

    /**
     * The {@link IPacketGate}'s of this protocol/version should be initiated and returned here
     *
     * @return The initiated {@link IPacketGate}'s
     */
    Set<IPacketGate> packetGates();
}
