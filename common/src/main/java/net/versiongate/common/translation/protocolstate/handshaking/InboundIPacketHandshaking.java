package net.versiongate.common.translation.protocolstate.handshaking;

import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.api.translation.IPacketType;

public enum InboundIPacketHandshaking implements IPacketType {

    HANDSHAKE(0x00);

    private final int id;

    InboundIPacketHandshaking(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public PacketBound getPacketBound() {
        return PacketBound.IN;
    }

    @Override
    public ProtocolState getStateApplication() {
        return ProtocolState.HANDSHAKING;
    }
}
