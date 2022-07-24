package net.versiongate.api.gate.gate;

import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.application.IPacketConsumer;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;

public interface IPacketGate extends IGate {

    /**
     * Adds a {@link IPacketConsumer} to this {@link IPacketGate}
     *
     * @param packetType The {@link IPacketType} we are consuming
     * @param consumer   The actual consumer
     */
    void packetConsumer(IPacketType packetType, IPacketConsumer consumer);

    /**
     * Attempt to translate the packet
     *
     * @param packet The actual {@link IPacket} that is being translated
     */
    void translate(IPacket packet);
}
