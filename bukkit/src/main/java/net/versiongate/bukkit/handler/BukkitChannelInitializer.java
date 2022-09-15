package net.versiongate.bukkit.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import java.lang.reflect.Method;
import java.util.List;
import net.versiongate.api.connection.IConnection;
import net.versiongate.common.connection.ConnectionManager;
import net.versiongate.common.platform.PlatformChannelInitializer;

public class BukkitChannelInitializer extends PlatformChannelInitializer {

    public BukkitChannelInitializer(ChannelInitializer<?> initializer) {
        super(new ConnectionManager(), initializer, "decoder", "encoder");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void pipelineEncoder(IConnection connection, ChannelPipeline pipeline) {
        final MessageToByteEncoder<ByteBuf> serverEncoder = (MessageToByteEncoder<ByteBuf>) pipeline.get(this.encoderName);
        pipeline.replace(this.encoderName, this.encoderName, new DefaultEncoder(connection, (out, context, message) -> {
            final Method method = MessageToByteEncoder.class.getDeclaredMethod("encode", ChannelHandlerContext.class, Object.class, ByteBuf.class);
            method.setAccessible(true);
            method.invoke(serverEncoder, context, message, out);
        }));
    }

    @Override
    public void pipelineDecoder(IConnection connection, ChannelPipeline pipeline) {
        final ByteToMessageDecoder serverDecoder = (ByteToMessageDecoder) pipeline.get(this.decoderName);
        pipeline.replace(this.decoderName, this.decoderName, new DefaultDecoder(connection, (out, context, message) -> {
            final Method method = ByteToMessageDecoder.class.getDeclaredMethod("decode", ChannelHandlerContext.class, ByteBuf.class, List.class);
            method.setAccessible(true);
            method.invoke(serverDecoder, context, message, out);
        }));
    }
}
