package net.versiongate.api.gate.gate;

import net.versiongate.api.connection.IConnection;
import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.IGateType;
import net.versiongate.api.gate.application.IPacketConsumer;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;

public interface IPacketGate extends IGate {

    IGateType getGateType();

    void setGateType(IGateType gateType);

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

    /**
     * Create a new packet that can be sent to the client
     *
     * @param connection The client we are creating the packet for
     * @param packetType The packet type
     * @return The packet that was created
     */
    IPacket createPacket(IConnection connection, IPacketType packetType);
}
