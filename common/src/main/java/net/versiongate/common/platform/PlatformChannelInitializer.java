package net.versiongate.common.platform;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.lang.reflect.Method;
import java.util.List;

public abstract class PlatformChannelInitializer extends ChannelInitializer<Channel> {
    protected final ChannelInitializer<?> initializer;

    protected static final String DECODER_NAME = "version-gate-decoder";
    protected static final String ENCODER_NAME = "version-gate-encoder";
    private static final Method INIT_CHANNEL_METHOD;

    static {
        try {
            INIT_CHANNEL_METHOD = ChannelInitializer.class.getDeclaredMethod("initChannel", Channel.class);
            INIT_CHANNEL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public PlatformChannelInitializer(ChannelInitializer<?> initializer) {
        this.initializer = initializer;
    }

    /**
     * An abstraction to modify the {@link ChannelPipeline}, specifically to set an encoder
     *
     * @param pipeline The {@link ChannelPipeline } that will have its encoder replaced
     */
    public abstract void pipelineEncoder(ChannelPipeline pipeline);

    /**
     * An abstraction to modify the {@link ChannelPipeline}, specifically to set a decoder
     *
     * @param pipeline The {@link ChannelPipeline } that will have its decoder replaced
     */
    public abstract void pipelineDecoder(ChannelPipeline pipeline);

    @Override
    protected void initChannel(Channel channel) throws Exception {
        INIT_CHANNEL_METHOD.invoke(this.initializer, channel);

        final ChannelPipeline pipeline = channel.pipeline();
        this.pipelineEncoder(pipeline);
        this.pipelineDecoder(pipeline);
    }

    public static class Decoder extends MessageToMessageDecoder<ByteBuf> {

        @Override
        protected void decode(ChannelHandlerContext context, ByteBuf message, List<Object> out) throws Exception {
            out.add(message.retain());
        }
    }

    public static class Encoder extends MessageToMessageEncoder<ByteBuf> {

        @Override
        protected void encode(ChannelHandlerContext context, ByteBuf message, List<Object> out) throws Exception {
            out.add(message.retain());
        }
    }
}
