package net.versiongate.standalone.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import net.versiongate.api.connection.IConnection;
import net.versiongate.common.connection.ConnectionManager;
import net.versiongate.common.platform.PlatformChannelInitializer;
import net.versiongate.standalone.netty.connection.NettyChannelInitializer;

public class StandaloneChannelInitializer extends PlatformChannelInitializer {

    public StandaloneChannelInitializer(ChannelInitializer<?> initializer) {
        super(new ConnectionManager(), initializer);
    }

    @Override
    public void pipelineEncoder(IConnection connection, ChannelPipeline pipeline) {
        pipeline.addLast(ENCODER_NAME, new DefaultEncoder(connection));
    }

    @Override
    public void pipelineDecoder(IConnection connection, ChannelPipeline pipeline) {
        pipeline.addBefore(NettyChannelInitializer.DECODER_NAME, DECODER_NAME, new DefaultDecoder(connection));
    }
}
