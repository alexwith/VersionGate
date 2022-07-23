package net.versiongate.common.translation.protocolstate.type.login;

import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;
import net.versiongate.api.translation.IPacketType;

public enum OutboundPacketLogin implements IPacketType {

    DISCONNECT(0x00),
    ENCRYPTION_REQUEST(0x01),
    LOGIN_SUCCESS(0x02),
    SET_COMPRESSION(0x03),
    LOGIN_PLUGIN_REQUEST(0x04);

    private final int id;

    OutboundPacketLogin(int id) {
        this.id = id;
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
        return ProtocolState.LOGIN;
    }
}
