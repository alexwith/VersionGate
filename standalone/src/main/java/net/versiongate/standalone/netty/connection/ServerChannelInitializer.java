package net.versiongate.standalone.netty.connection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;

public class ServerChannelInitializer extends ChannelInitializer<Channel> {
    private final ChannelHandlerContext context;

    public ServerChannelInitializer(ChannelHandlerContext context) {
        this.context = context;
    }

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline().addLast(new ServerChannelHandler(this.context.channel()));
    }

    private static class ServerChannelHandler extends ChannelDuplexHandler {
        private final Channel channel;

        public ServerChannelHandler(Channel originalChannel) {
            this.channel = originalChannel;
        }

        @Override
        public void channelRead(ChannelHandlerContext context, Object message) {
            final ByteBuf readBuffer = (ByteBuf) message;
            final byte[] bytes = new byte[readBuffer.readableBytes()];
            readBuffer.readBytes(bytes);

            final ByteBuf writeBuffer = UnpooledByteBufAllocator.DEFAULT.buffer();
            this.channel.writeAndFlush(writeBuffer.writeBytes(bytes));
        }
    }
}
