package net.versiongate.standalone.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import java.net.SocketAddress;
import java.security.GeneralSecurityException;
import java.util.concurrent.ThreadLocalRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.gate.GateType;
import net.versiongate.common.packet.PacketTypes;
import net.versiongate.common.translation.protocolstate.type.login.InboundPacketLogin;
import net.versiongate.common.translation.protocolstate.type.login.OutboundPacketLogin;
import net.versiongate.standalone.StandalonePlatform;
import net.versiongate.standalone.encryption.StandaloneCipher;
import net.versiongate.standalone.netty.cipher.CipherDecoder;
import net.versiongate.standalone.netty.cipher.CipherEncoder;
import net.versiongate.standalone.util.ChannelUtil;

/*
 * This class contains a lot of temporary weird logic for getting a working prototype
 */
public class StandaloneConnection extends ChannelInboundHandlerAdapter {
    private final SocketAddress address;
    private final IConnection connection;
    private final Channel channel;

    private Channel outboundChannel;
    private boolean encryptionSent;
    private boolean encryptionEnabled;

    public StandaloneConnection(SocketAddress address, IConnection connection, Channel channel) {
        this.address = address;
        this.connection = connection;
        this.channel = channel;
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
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        if (!this.outboundChannel.isActive()) {
            return;
        }

        if (message instanceof ByteBuf) {
            final ByteBuf packet = ((ByteBuf) message).copy();
            if (packet.isReadable() && !this.encryptionSent) {
                BufferAdapter.VAR_INT.read(packet); // skip length

                final int packetId = BufferAdapter.VAR_INT.read(packet);
                final IPacketType packetType = PacketTypes.getPacketType(GateType.VERSION1_8, this.connection.getProtocolState(), PacketBound.IN, packetId);
                if (packetType == InboundPacketLogin.LOGIN_START) {
                    this.encryptionSent = true;
                    this.channel.eventLoop().execute(this::sendEncryptionRequestPacket);
                }
            }

            if (packet.isReadable() && !this.encryptionEnabled) {
                BufferAdapter.VAR_INT.read(packet); // skip length

                final int packetId = BufferAdapter.VAR_INT.read(packet);
                final IPacketType packetType = PacketTypes.getPacketType(GateType.VERSION1_8, this.connection.getProtocolState(), PacketBound.IN, packetId);
                if (packetType == InboundPacketLogin.ENCRYPTION_RESPONSE) {
                    this.encryptionEnabled = true;
                    this.enableEncryption(packet);
                }
            }
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

    private void enableEncryption(ByteBuf encryptionResponsePacket) throws GeneralSecurityException {
        final byte[] sharedSecret = BufferAdapter.BYTE_ARRAY.read(encryptionResponsePacket);

        final Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, StandalonePlatform.ENCRYPTION_KEY.getPrivate());
        final byte[] decryptedSharedSecret = rsaCipher.doFinal(sharedSecret);

        final SecretKey key = new SecretKeySpec(decryptedSharedSecret, "AES");
        final StandaloneCipher decryptionCipher = new StandaloneCipher(false, key);
        final StandaloneCipher encryptionCipher = new StandaloneCipher(true, key);

        this.channel.pipeline()
            .addBefore(StandaloneChannelInitializer.FRAME_DECODER_NAME, null, new CipherDecoder(decryptionCipher))
            .addBefore(StandaloneChannelInitializer.FRAME_ENCODER_NAME, null, new CipherEncoder(encryptionCipher));
    }

    private void sendEncryptionRequestPacket() {
        final int packetLength = 171; // magic number
        final int packetId = OutboundPacketLogin.ENCRYPTION_REQUEST.getId();

        final byte[] publicKey = StandalonePlatform.ENCRYPTION_KEY.getPublic().getEncoded();

        final byte[] verifyToken = new byte[4];
        ThreadLocalRandom.current().nextBytes(verifyToken);

        final ByteBuf packet = Unpooled.buffer();
        BufferAdapter.VAR_INT.write(packet, packetLength);
        BufferAdapter.VAR_INT.write(packet, packetId);
        BufferAdapter.STRING.write(packet, "");
        BufferAdapter.BYTE_ARRAY.write(packet, publicKey);
        BufferAdapter.BYTE_ARRAY.write(packet, verifyToken);

        this.channel.writeAndFlush(packet, this.channel.voidPromise());
    }
}
