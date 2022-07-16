package net.versiongate.standalone.netty.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.versiongate.common.util.ProtocolUtil;
import net.versiongate.standalone.Main;
import net.versiongate.standalone.netty.NettyServer;
import net.versiongate.standalone.netty.connection.ConnectionContext;
import net.versiongate.standalone.netty.connection.ServerChannelInitializer;
import net.versiongate.standalone.netty.enums.State;

public class MinecraftDecoder extends ChannelInboundHandlerAdapter {
    private final Map<ChannelHandlerContext, ConnectionContext> connectionContexts = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        final ByteBuf readBuffer = (ByteBuf) message;
        final ConnectionContext connectionContext = this.connectionContexts.computeIfAbsent(context, ($) -> new ConnectionContext());
        final State state = connectionContext.getState();
        if (state == State.HANDSHAKE) {
            final int packetLength = ProtocolUtil.readVarInt(readBuffer);
            final int packetId = ProtocolUtil.readVarInt(readBuffer);
            if (packetId != 0x00) {
                return;
            }

            this.handshake(context, connectionContext, readBuffer, (writeBuffer) -> {
                ProtocolUtil.writeVarInt(writeBuffer, packetLength);
                ProtocolUtil.writeVarInt(writeBuffer, packetId);
            });
        } else {
            final Channel target = connectionContext.getTarget();
            final byte[] bytes = new byte[readBuffer.readableBytes()];
            readBuffer.readBytes(bytes);

            final ByteBuf writeBuffer = PooledByteBufAllocator.DEFAULT.buffer();
            target.writeAndFlush(writeBuffer.writeBytes(bytes));
        }
    }

    private void handshake(ChannelHandlerContext context, ConnectionContext connectionContext, ByteBuf readBuffer, Consumer<ByteBuf> replacer) {
        final Bootstrap bootstrap = new Bootstrap()
            .group(NettyServer.WORKER_GROUP)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
            .handler(new ServerChannelInitializer(context))
            .option(ChannelOption.TCP_NODELAY, true);

        final ChannelFuture channelFuture = bootstrap.connect(Main.TARGET_ADDRESS);
        channelFuture.addListener((ChannelFutureListener) (future) -> {
            if (future.isSuccess()) {
                final ByteBuf writeBuffer = PooledByteBufAllocator.DEFAULT.buffer();
                replacer.accept(writeBuffer);

                while (readBuffer.readableBytes() > 0) {
                    writeBuffer.writeByte(readBuffer.readByte());
                }

                future.channel().writeAndFlush(writeBuffer);
                connectionContext.setTarget(channelFuture.channel());
                connectionContext.setState(State.LOGIN); // we don't really care since this is a test environment
            } else {
                System.out.println("Disconnect");
                future.cause().printStackTrace();

                context.close();
                channelFuture.channel().close();
            }
        });
    }
}
