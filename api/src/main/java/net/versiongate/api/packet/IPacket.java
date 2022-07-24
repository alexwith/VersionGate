package net.versiongate.api.packet;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferType;
import net.versiongate.api.connection.IConnection;

public interface IPacket {

    /**
     * Get the {@link IConnection} the packet involves
     *
     * @return The {@link IConnection}
     */
    IConnection getConnection();

    /**
     * Gets the {@link IPacketType} of this packet
     *
     * @return The {@link IPacketType}
     */
    IPacketType getType();

    /**
     * Cancels the packet
     */
    void cancel();

    /**
     * Returns if the packet is cancelled or not
     *
     * @return Is the packet cancelled
     */
    boolean isCancelled();

    /**
     * This will write the packet contents to a buffer, and should be called once translation is complete
     *
     * @param buffer The buffer that will be written to
     */
    void writeTo(ByteBuf buffer);

    /**
     * Reads, then proceeds to replace the {@link BufferType} in the packet contents
     *
     * @param type The {@link BufferType}
     * @param <T>  The generic type of the action
     * @return What is read from the buffer
     */
    <T> T readWrite(BufferType type);

    /**
     * Reads from the packet contents
     *
     * @param type The {@link BufferType}
     * @param <T>  The generic type of the action
     * @return What is read from the buffer
     */
    <T> T read(BufferType type);

    /**
     * Writes to the packet contents
     *
     * @param type  The {@link BufferType}
     * @param value The value that is written
     * @param <T>   The generic type of the action
     */
    <T> void write(BufferType type, T value);

}
