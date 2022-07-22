package net.versiongate.standalone.netty.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import net.versiongate.standalone.netty.codec.MinecraftDecoder;

public class NettyChannelInitializer extends ChannelInitializer<Channel> {

    public static final String DECODER_NAME = "minecraft-decoder";
    public static final String ENCODER_NAME = "minecraft-encoder";

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline().addFirst(DECODER_NAME, new MinecraftDecoder());
    }
}
