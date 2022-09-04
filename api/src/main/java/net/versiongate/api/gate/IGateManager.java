package net.versiongate.api.gate;

import java.util.Set;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.api.gate.version.ProtocolVersion;

public interface IGateManager {

    ProtocolVersion getProtocolVersion();

    void setProtocolVersion(ProtocolVersion protocolVersion);

    /**
     * Check if the {@link ProtocolVersion} of the server has been set
     *
     * @return Has the {@link ProtocolVersion} been set
     */
    boolean hasProtocolVersion();

    /**
     * Gets handshaking gates loaded on {@link #initialLoad()}
     *
     * @return The handshaking gates
     */
    Set<IGate> getHandshakingGates();

    /**
     * Gets the relevant gates for the specified {@link ProtocolVersion}
     *
     * @param protocolVersion The {@link ProtocolVersion} we are querying
     * @return This can be null, but should always be returning gates
     */
    Set<IGate> getVersionGates(ProtocolVersion protocolVersion);

    /**
     * The initial loading of all {@link IPacketGate}'s
     */
    void initialLoad();
}
