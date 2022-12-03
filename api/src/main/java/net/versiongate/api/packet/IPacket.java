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
    void schema(BufferAdapter<?>... types);

    /**
     * Reads a field from the content cache
     *
     * @param index The index of the field
     * @param <T>   The type of the field
     * @return The actual field value
     */
    <T> T readField(int index);

    /**
     * Writes a field in the content cache
     *
     * @param index The index of the field
     * @param value The value of the field
     * @param <T>   The type of the value
     */
    <T> void writeField(int index, T value);

    /**
     * Set field adapter
     *
     * @param index
     * @param type
     */
    void setFieldAdapter(int index, BufferAdapter<?> type);

    /**
     * Send the packet with force
     */
    void send();
}
