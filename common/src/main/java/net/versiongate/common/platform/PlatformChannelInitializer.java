package net.versiongate.common.platform;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import java.lang.reflect.Method;
import java.util.List;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.connection.IConnectionManager;
import net.versiongate.api.enums.PacketBound;

public abstract class PlatformChannelInitializer extends ChannelInitializer<Channel> {
    protected final IConnectionManager connectionManager;
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

    public PlatformChannelInitializer(IConnectionManager connectionManager, ChannelInitializer<?> initializer) {
        this.connectionManager = connectionManager;
        this.initializer = initializer;
    }

    /**
     * An abstraction to modify the {@link ChannelPipeline}, specifically to set an encoder
     *
     * @param connection The {@link IConnection} representing who connected
     * @param pipeline   The {@link ChannelPipeline } that will have its encoder replaced
     */
    public abstract void pipelineEncoder(IConnection connection, ChannelPipeline pipeline);

    /**
     * An abstraction to modify the {@link ChannelPipeline}, specifically to set a decoder
     *
     * @param connection The {@link IConnection} representing who connected
     * @param pipeline   The {@link ChannelPipeline } that will have its decoder replaced
     */
    public abstract void pipelineDecoder(IConnection connection, ChannelPipeline pipeline);

    @Override
    protected void initChannel(Channel channel) throws Exception {
        INIT_CHANNEL_METHOD.invoke(this.initializer, channel);

        final IConnection connection = this.connectionManager.notifyConnection(channel);
        final ChannelPipeline pipeline = channel.pipeline();
        this.pipelineEncoder(connection, pipeline);
        this.pipelineDecoder(connection, pipeline);
    }

    public static class DefaultEncoder extends MessageToByteEncoder<Object> {
        private final IConnection connection;
        private final EncoderOutConsumer outConsumer;

        public DefaultEncoder(IConnection connection, EncoderOutConsumer outConsumer) {
            this.connection = connection;
            this.outConsumer = outConsumer;
        }

        @Override
        protected void encode(ChannelHandlerContext context, Object message, ByteBuf out) {
            if (message instanceof ByteBuf) {
                out.writeBytes((ByteBuf) message);
            } else {
                try {
                    this.outConsumer.consume(out, context, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.connection.translate(out, PacketBound.OUT);
        }

        public interface EncoderOutConsumer {

            void consume(ByteBuf out, ChannelHandlerContext context, Object message) throws Exception;
        }
    }

    public static class DefaultDecoder extends ByteToMessageDecoder {
        private final IConnection connection;
        private final DecoderOutConsumer outConsumer;

        public DefaultDecoder(IConnection connection, DecoderOutConsumer outConsumer) {
            this.connection = connection;
            this.outConsumer = outConsumer;
        }

        @Override
        protected void decode(ChannelHandlerContext context, ByteBuf message, List<Object> out) {
            if (!message.isReadable()) {
                return;
            }

            final boolean shouldTranslate = this.connection.shouldTranslate();
            final ByteBuf translationBuffer = context.alloc().buffer();
            if (shouldTranslate) {
                translationBuffer.writeBytes(message);

                this.connection.translate(translationBuffer, PacketBound.IN);
            }

            try {
                this.outConsumer.consume(out, context, translationBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (shouldTranslate) {
                    translationBuffer.release();
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
            cause.printStackTrace();
        }
    }

    public interface DecoderOutConsumer {

        void consume(List<Object> out, ChannelHandlerContext context, ByteBuf message) throws Exception;
    }
}