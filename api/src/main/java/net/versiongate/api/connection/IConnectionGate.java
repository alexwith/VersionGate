package net.versiongate.api.connection;

import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.api.packet.IPacket;

public interface IConnectionGate {

    /**
     * The entry point to translating packets
     *
     * @param packet The actual {@link IPacket} that is being translated
     */
    void translatePacket(IPacket packet);

    /**
     * Called when the protocol version is set in the {@link IConnection}
     *
     * @param protocolVersion The protocol version that was set
     */
    void onSetProtocolVersion(ProtocolVersion protocolVersion);
}
