package net.versiongate.standalone.netty.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class NettyChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline().addLast(new MinecraftDecoder());
    }
}
