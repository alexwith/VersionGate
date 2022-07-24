package net.versiongate.api.gate.translation;

import net.versiongate.api.packet.IPacket;

public interface IPacketConsumer {

    /**
     * Consumes a {@link IPacket} to translate it
     *
     * @param packet The {@link IPacket} being consumed
     * @throws Exception If the consumer throws anything, we want to have control
     */
    void translate(IPacket packet) throws Exception;
}