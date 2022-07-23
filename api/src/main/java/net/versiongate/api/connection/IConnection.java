package net.versiongate.api.connection;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.enums.ProtocolState;

public interface IConnection {

    /**
     * Gets the current protocol version
     *
     * @return The protocol version
     */
    int getProtocolVersion();

    /**
     * Gets the protocol server
     *
     * @param protocolVersion The new protocol version
     */
    void setProtocolVersion(int protocolVersion);

    /**
     * Gets the {@link ProtocolState} the connection is in
     *
     * @return The {@link ProtocolState}
     */
    ProtocolState getProtocolState();

    /**
     * Sets the {@link ProtocolState} the connection is in
     *
     * @param protocolState The new {@link ProtocolState}
     */
    void setProtocolState(ProtocolState protocolState);

    /**
     * Disconnects the connection
     */
    void disconnect();

    /**
     * Starts and ends the translation pipeline
     *
     * @param buffer The buffer allocated by the pipeline codec
     * @param bound  Where the packet is bound.
     */
    void translate(ByteBuf buffer, PacketBound bound);

    /**
     * If the connection should translate packets or not
     *
     * @return If it should translate
     */
    boolean shouldTranslate();
}
