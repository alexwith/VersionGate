package net.versiongate.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.Map;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.platform.Platform;
import net.versiongate.common.platform.PlatformChannelInitializer;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

public class Packet implements IPacket {
    private final IConnection connection;
    private final ByteBuf contentBuffer;
    private final List<Object> content = FastList.newList();
    private final Map<Integer, BufferAdapter> bufferAdapters = UnifiedMap.newMap();

    private IPacketType type;
    private boolean isCancelled;

    public Packet(IConnection connection, IPacketType type, ByteBuf contentBuffer) {
        this.connection = connection;
        this.type = type;
        this.contentBuffer = contentBuffer;
    }

    @Override
    public IConnection getConnection() {
        return this.connection;
    }

    @Override
    public IPacketType getType() {
        return this.type;
    }

    @Override
    public void setType(IPacketType type) {
        this.type = type;
    }

    @Override
    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void writeTo(ByteBuf buffer) {
        if (this.contentBuffer == null) {
            return;
        }

        BufferAdapter.VAR_INT.write(buffer, this.type.getId());
        System.out.println("wrote id: " + this.type.getId());

        for (int i = 0; i < this.content.size(); i++) {
            final BufferAdapter type = this.bufferAdapters.get(i);
            final Object value = this.content.get(i);
            type.write(buffer, value);
        }

        buffer.writeBytes(this.contentBuffer);
    }

    @Override
    public void schema(BufferAdapter... types) {
        this.bufferAdapters.clear();

        for (int i = 0; i < types.length; i++) {
            final BufferAdapter type = types[i];
            final Object value = this.contentBuffer.isReadable() ? type.read(this.contentBuffer) : null;
            this.content.add(i, value);
            this.bufferAdapters.put(i, type);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getField(int index) {
        return (T) this.content.get(index);
    }

    @Override
    public <T> void setField(int index, T value) {
        this.content.set(index, value);
    }

    @Override
    public void send() {
        if (this.isCancelled) {
            return;
        }

        System.out.println("Sending packet: " + this.type + " -> " + this.content.size());

        final ByteBuf buffer = this.toBuffer();
        final Channel channel = this.connection.getChannel();
        //channel.eventLoop().submit(() -> {
        final PlatformChannelInitializer initializer = Platform.get().getInjector().getChannelInitializer();
        if (initializer == null) {
            throw new IllegalStateException("The PlatformChannelInitializer is null while sending packet");
        }

        final boolean isOutbound = this.type.getPacketBound() == PacketBound.OUT;
        final ChannelHandlerContext context = channel.pipeline().context(isOutbound ? initializer.getDecoderName() : initializer.getEncoderName());
        System.out.println("we have the context: " + context + " -> outbound:" + isOutbound);
        if (isOutbound) {
            context.fireChannelRead(buffer);
        } else {
            context.writeAndFlush(buffer);
        }
        System.out.println("completed context");
        //});
    }

    private ByteBuf toBuffer() {
        final Channel channel = this.connection.getChannel();
        final ByteBuf buffer = channel.alloc().buffer();
        try {
            this.writeTo(buffer);
            return buffer.retain();
        } finally {
            buffer.release();
        }
    }
}
