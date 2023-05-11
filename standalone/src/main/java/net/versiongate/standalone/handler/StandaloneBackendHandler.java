package net.versiongate.standalone.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.versiongate.standalone.util.ChannelUtil;

public class StandaloneBackendHandler extends ChannelInboundHandlerAdapter {
    private final Channel inboundChannel;

    public StandaloneBackendHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
        context.read();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        this.inboundChannel.writeAndFlush(message).addListener((ChannelFutureListener) (future) -> {
            if (future.isSuccess()) {
                context.channel().read();
                return;
            }

            future.channel().close();
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        ChannelUtil.closeOnFlush(context.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        ChannelUtil.closeOnFlush(context.channel());
    }
}
