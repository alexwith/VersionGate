package net.versiongate.api.packet;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.connection.IConnection;

public interface IPacket {

    IConnection getConnection();

    IPacketType getType();

    void setType(IPacketType type);

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
     * Creates the schema for the packet, so it can cache its fields
     *
     * @param types The packet schema
     */
    void schema(BufferAdapter... types);

    /**
     * Gets a field from the content cache
     *
     * @param index The index of the field
     * @param <T>   The type of the field
     * @return The actual field value
     */
    <T> T getField(int index);

    /**
     * Sets a field in the content cache
     *
     * @param index The index of the field
     * @param value The value of the field
     * @param <T>   The type of the value
     */
    <T> void setField(int index, T value);
}
