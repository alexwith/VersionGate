package net.versiongate.common.translation.version1_9.type;

import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.packet.IPacketType;

public enum InboundPacket1_9 implements IPacketType {

    TELEPORT_CONFIRM(0x00),
    TAB_COMPLETE(0x01),
    CHAT_MESSAGE(0x02),
    CLIENT_STATUS(0x03),
    CLIENT_SETTINGS(0x04),
    WINDOW_CONFIRMATION(0x05),
    CLICK_WINDOW_BUTTON(0x06),
    CLICK_WINDOW(0x07),
    CLOSE_WINDOW(0x08),
    PLUGIN_MESSAGE(0x09),
    INTERACT_ENTITY(0x0A),
    KEEP_ALIVE(0x0B),
    PLAYER_POSITION(0x0C),
    PLAYER_POSITION_AND_ROTATION(0x0D),
    PLAYER_ROTATION(0x0E),
    PLAYER_MOVEMENT(0x0F),
    VEHICLE_MOVE(0x10),
    STEER_BOAT(0x11),
    PLAYER_ABILITIES(0x12),
    PLAYER_DIGGING(0x13),
    ENTITY_ACTION(0x14),
    STEER_VEHICLE(0x15),
    RESOURCE_PACK_STATUS(0x16),
    HELD_ITEM_CHANGE(0x17),
    CREATIVE_INVENTORY_ACTION(0x18),
    UPDATE_SIGN(0x19),
    ANIMATION(0x1A),
    SPECTATE(0x1B),
    PLAYER_BLOCK_PLACEMENT(0x1C),
    USE_ITEM(0x1D);

    private final int id;

    InboundPacket1_9(int id) {
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
}
