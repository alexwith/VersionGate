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

    private static final String DECODER_NAME = "decoder";
    private static final String ENCODER_NAME = "encoder";

    public BukkitChannelInitializer(ChannelInitializer<?> initializer) {
        super(new ConnectionManager(), initializer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void pipelineEncoder(IConnection connection, ChannelPipeline pipeline) {
        final MessageToByteEncoder<ByteBuf> serverEncoder = (MessageToByteEncoder<ByteBuf>) pipeline.get(ENCODER_NAME);
        pipeline.replace(ENCODER_NAME, ENCODER_NAME, new DefaultEncoder(connection, (out, context, message) -> {
            final Method method = MessageToByteEncoder.class.getDeclaredMethod("encode", ChannelHandlerContext.class, Object.class, ByteBuf.class);
            method.setAccessible(true);
            method.invoke(serverEncoder, context, message, out);
        }));
    }

    @Override
    public void pipelineDecoder(IConnection connection, ChannelPipeline pipeline) {
        final ByteToMessageDecoder serverDecoder = (ByteToMessageDecoder) pipeline.get(DECODER_NAME);
        pipeline.replace(DECODER_NAME, DECODER_NAME, new DefaultDecoder(connection, (out, context, message) -> {
            final Method method = ByteToMessageDecoder.class.getDeclaredMethod("decode", ChannelHandlerContext.class, ByteBuf.class, List.class);
            method.setAccessible(true);
            method.invoke(serverDecoder, context, message, out);
        }));
    }
}
