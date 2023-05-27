package net.versiongate.standalone.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import java.net.SocketAddress;
import net.versiongate.standalone.util.ChannelUtil;

public class StandaloneConnection extends ChannelInboundHandlerAdapter {
    private final SocketAddress address;

    private Channel outboundChannel;

    public StandaloneConnection(SocketAddress address) {
        this.address = address;
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
        final Channel inboundChannel = context.channel();

        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(inboundChannel.eventLoop())
            .channel(context.channel().getClass())
            .handler(new StandaloneBackendHandler(inboundChannel))
            .option(ChannelOption.AUTO_READ, false);

        final ChannelFuture future = bootstrap.connect(this.address);
        this.outboundChannel = future.channel();

        future.addListener((ChannelFutureListener) (otherFuture) -> {
            if (otherFuture.isSuccess()) {
                inboundChannel.read();
                return;
            }

            inboundChannel.close();
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (!this.outboundChannel.isActive()) {
            return;
        }

        this.outboundChannel.writeAndFlush(message).addListener((ChannelFutureListener) (future) -> {
            if (future.isSuccess()) {
                context.channel().read();
                return;
            }

            future.channel().close();
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        if (this.outboundChannel == null) {
            return;
        }

        ChannelUtil.closeOnFlush(this.outboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        ChannelUtil.closeOnFlush(context.channel());
    }
}
