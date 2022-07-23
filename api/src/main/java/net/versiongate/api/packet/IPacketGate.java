package net.versiongate.api.packet;

import java.util.function.Consumer;
import net.versiongate.api.translation.IPacketType;

public interface IPacketGate {

    /**
     * Where everything in the gate should be applied
     */
    void load();

    /**
     * Adds a translator to this {@link IPacketGate}
     *
     * @param packetType The {@link IPacketType} we are translating
     * @param consumer The actual translator
     */
    void packetTranslation(IPacketType packetType, Consumer<IPacket> consumer);
}
