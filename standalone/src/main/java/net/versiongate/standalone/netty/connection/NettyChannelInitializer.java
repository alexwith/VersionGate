package net.versiongate.standalone.netty.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class NettyChannelInitializer extends ChannelInitializer<Channel> {

    private static final String DECODER_NAME = "minecraft-decoder";

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline().addLast(DECODER_NAME, new MinecraftDecoder());
    }
}
