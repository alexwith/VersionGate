package net.versiongate.api.connection;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.enums.PacketBound;

public interface IConnection {

    /**
     * Disconnects the connection
     */
    void disconnect();

    /**
     * Starts and ends the translation pipeline
     *
     * @param buffer The buffer allocated by the pipeline codec
     * @param bound Where the packet is bound.
     */
    void translate(ByteBuf buffer, PacketBound bound);
}
