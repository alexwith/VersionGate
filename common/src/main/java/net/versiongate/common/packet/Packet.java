package net.versiongate.common.packet;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.packet.IPacket;

public class Packet implements IPacket {
    private final int id;
    private final int length;
    private final ByteBuf contentBuffer;

    public Packet(int id, int length, ByteBuf contents) {
        this.id = id;
        this.length = length;
        this.contentBuffer = contents;
    }

    @Override
    public void writeTo(ByteBuf buffer) {
        //ProtocolUtil.writeVarInt(buffer, this.length);
        //ProtocolUtil.writeVarInt(buffer, this.id);

        buffer.writeBytes(this.contentBuffer);
    }
}
