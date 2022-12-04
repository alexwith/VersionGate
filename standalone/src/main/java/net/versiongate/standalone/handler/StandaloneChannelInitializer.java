package net.versiongate.standalone.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.connection.IConnectionManager;
import net.versiongate.common.platform.PlatformChannelInitializer;

public class StandaloneChannelInitializer extends PlatformChannelInitializer {

    public StandaloneChannelInitializer(IConnectionManager connectionManager, ChannelInitializer<?> initializer, String decoderName, String encoderName) {
        super(connectionManager, initializer, decoderName, encoderName);
    }

    @Override
    public void pipelineEncoder(IConnection connection, ChannelPipeline pipeline) {

    }

    @Override
    public void pipelineDecoder(IConnection connection, ChannelPipeline pipeline) {

    }
}
