package net.versiongate.common.packet;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.packet.IPacket;
import net.versiongate.common.util.ProtocolUtil;

public class Packet implements IPacket {
    private final int id;
    private final ByteBuf contentBuffer;

    public Packet(int id, ByteBuf contents) {
        this.id = id;
        this.contentBuffer = contents;
    }

    @Override
    public void writeTo(ByteBuf buffer) {
        ProtocolUtil.writeVarInt(buffer, this.id);

        buffer.writeBytes(this.contentBuffer);
    }
}
