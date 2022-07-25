package net.versiongate.api.gate;

import java.util.Set;
import net.versiongate.api.gate.version.ProtocolVersion;

public interface IGateManager {

    /**
     * Gets handshaking gates loaded on {@link #initialLoad()}
     *
     * @return The handshaking gates
     */
    Set<IGate> getHandshakingGates();

    /**
     * Gets the {@link ProtocolVersion} of the server
     *
     * @return The {@link ProtocolVersion}
     */
    ProtocolVersion getProtocolVersion();

    /**
     * Sets the {@link ProtocolVersion} of the server
     *
     * @param protocolVersion The {@link ProtocolVersion}
     */
    void setProtocolVersion(ProtocolVersion protocolVersion);

    /**
     * Check if the {@link ProtocolVersion} of the server has been set
     *
     * @return Has the {@link ProtocolVersion} been set
     */
    boolean hasProtocolVersion();

    /**
     * The initial loading of for instance handshaking packets
     */
    void initialLoad();
}
