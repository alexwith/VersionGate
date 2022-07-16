package net.versiongate.standalone.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import net.versiongate.common.platform.PlatformChannelInitializer;
import net.versiongate.standalone.netty.connection.NettyChannelInitializer;

public class StandaloneChannelInitializer extends PlatformChannelInitializer {

    public StandaloneChannelInitializer(ChannelInitializer<?> initializer) {
        super(initializer);
    }

    @Override
    public void pipelineEncoder(ChannelPipeline pipeline) {
        pipeline.addLast(ENCODER_NAME, new Encoder());
    }

    @Override
    public void pipelineDecoder(ChannelPipeline pipeline) {
        pipeline.addBefore(NettyChannelInitializer.DECODER_NAME, DECODER_NAME, new Decoder());
    }
}
