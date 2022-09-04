package net.versiongate.api.gate.gate;

import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.IGateType;
import net.versiongate.api.gate.application.IPacketConsumer;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;

public interface IPacketGate extends IGate {

    IGateType getGateType();

    void setGateType(IGateType gateType);

    /**
     * Finds the mapped packet from the {@link IPacketGate#getGateType()} and applies it to {@link IPacketGate#packetConsumer(IPacketType, IPacketType,
     * IPacketConsumer)}
     *
     * @param packetType The {@link IPacketType} that we are finding the mapping for
     * @param consumer   The {@link IPacketConsumer} that we will pass to {@link IPacketGate#packetConsumer(IPacketType, IPacketType, IPacketConsumer)}
     */
    default void packetConsumer(IPacketType packetType, IPacketConsumer consumer) {
        final IPacketType mappedPacketType = this.getGateType().getMappedPacketType(packetType);
        this.packetConsumer(packetType, mappedPacketType, consumer);
    }

    /**
     * Adds a {@link IPacketConsumer} and the mapped {@link IPacketType} to this {@link IPacketGate}
     *
     * @param packetType       The {@link IPacketType} we are consuming
     * @param mappedPacketType The {@link IPacketType} that is the equivalent of the packet for the respective version
     * @param consumer         The actual consumer
     */
    void packetConsumer(IPacketType packetType, IPacketType mappedPacketType, IPacketConsumer consumer);

    /**
     * Attempt to translate the packet
     *
     * @param packet The actual {@link IPacket} that is being translated
     */
    void translate(IPacket packet);
}
