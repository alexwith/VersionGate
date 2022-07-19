package net.versiongate.api.packet;

import io.netty.buffer.ByteBuf;

public interface IPacket {

    /**
     * This will write the packet contents to a buffer, and should be called once translation is complete
     *
     * @param buffer The buffer that will be written to
     */
    void writeTo(ByteBuf buffer);
}
