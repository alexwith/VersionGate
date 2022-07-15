package net.versiongate.standalone.enums;

import net.versiongate.standalone.packet.PacketRegistry;

public enum State {

    HANDSHAKE(PacketRegistry.HANDSHAKE),
    STATUS(PacketRegistry.STATUS),
    LOGIN(PacketRegistry.LOGIN),
    PLAY(PacketRegistry.PLAY);

    private final PacketRegistry registry;

    State(PacketRegistry registry) {
        this.registry = registry;
    }

    public PacketRegistry registry() {
        return this.registry;
    }

    public static State from(String name) {
        return valueOf(name.toUpperCase());
    }
}
