package net.versiongate.standalone.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public class ChannelUtil {

    public static void closeOnFlush(Channel channel) {
        if (!channel.isActive()) {
            return;
        }

        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
