package net.versiongate.api.gate;

import java.util.Set;
import net.versiongate.api.gate.gate.IProtocolGate;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.api.packet.IPacketType;

public interface IGateType {

    ProtocolVersion getProtocolVersion();

    IProtocolGate getProtocolGate();

    Set<IPacketType> getInbound();

    Set<IPacketType> getOutbound();

    /**
     * Get the {@link IPacketType} that is mapped to the {@link IPacketType} you pass
     *
     * @param packetType The {@link IPacketType} that we need to map
     * @return Will return null if it is not present
     */
    IPacketType getMappedPacketType(IPacketType packetType);

    void mapPacketTypes();
}
