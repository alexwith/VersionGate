package net.versiongate.api.connection;

import net.versiongate.api.gate.IGate;
import net.versiongate.api.packet.IPacket;

public interface IConnectionGate {

    /**
     * Adds a gate to the {@link IConnectionGate}
     *
     * @param gate The {@link IGate} being added
     */
    void addGate(IGate gate);

    /**
     * The entry point to translating packets
     *
     * @param packet The actual {@link IPacket} that is being translated
     */
    void translatePacket(IPacket packet);
}
