package net.versiongate.common.translation.protocolstate.type.handshaking;

import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.api.packet.IPacketType;

public enum InboundPacketHandshaking implements IPacketType {

    HANDSHAKE(0x00);

    private final int id;

    InboundPacketHandshaking(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name();
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
