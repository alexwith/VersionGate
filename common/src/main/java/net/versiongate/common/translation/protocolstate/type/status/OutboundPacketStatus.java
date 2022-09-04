package net.versiongate.common.translation.protocolstate.type.status;

import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.api.packet.IPacketType;

public enum OutboundPacketStatus implements IPacketType {

    STATUS_RESPONSE(0x00),
    PING_RESPONSE(0x01);

    private final int id;

    OutboundPacketStatus(int id) {
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
        return PacketBound.OUT;
    }

    @Override
    public ProtocolState getStateApplication() {
        return ProtocolState.STATUS;
    }
}
