package net.versiongate.bukkit.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import net.versiongate.api.connection.IConnection;
import net.versiongate.common.connection.ConnectionManager;
import net.versiongate.common.platform.PlatformChannelInitializer;

public class BukkitChannelInitializer extends PlatformChannelInitializer {

    public BukkitChannelInitializer(ChannelInitializer<?> initializer) {
        super(new ConnectionManager(), initializer);
    }

    @Override
    public void pipelineEncoder(IConnection connection, ChannelPipeline pipeline) {
        //pipeline.addLast(ENCODER_NAME, new DefaultEncoder(connection));
    }

    @Override
    public void pipelineDecoder(IConnection connection, ChannelPipeline pipeline) {
        pipeline.replace("decoder", "decoder", new DefaultDecoder(connection));
    }
}
