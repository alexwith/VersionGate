package net.versiongate.standalone.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.security.GeneralSecurityException;
import java.util.List;
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
import net.versiongate.standalone.connection.StandaloneChannelInitializer;
import net.versiongate.standalone.encryption.StandaloneCipher;
import net.versiongate.standalone.netty.cipher.CipherDecoder;
import net.versiongate.standalone.netty.cipher.CipherEncoder;

// TODO: A lot here is temporary, currently working on making everything work
public class StandaloneDecoder extends ByteToMessageDecoder {
    private final Channel channel;
    private final IConnection connection;

    public StandaloneDecoder(Channel channel, IConnection connection) {
        this.channel = channel;
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf packet, List<Object> out) throws Exception {
        final boolean shouldTranslate = this.connection.shouldTranslate();
        if (!packet.isReadable() || !shouldTranslate) {
            context.fireChannelRead(packet);
            return;
        }

        final ByteBuf copiedPacket = packet.duplicate();

        final int packetId = BufferAdapter.VAR_INT.read(copiedPacket);
        final IPacketType packetType = PacketTypes.getPacketType(GateType.VERSION1_8, this.connection.getProtocolState(), PacketBound.IN, packetId);
        if (packetType == InboundPacketLogin.ENCRYPTION_RESPONSE) {
            this.enableEncryption(copiedPacket);
        }

        this.connection.translate(packet, PacketBound.IN);

        final ByteBuf finalBuffer = context.alloc().buffer();
        BufferAdapter.VAR_INT.write(finalBuffer, packet.readableBytes());
        finalBuffer.writeBytes(packet);

        context.fireChannelRead(finalBuffer);

        if (packetType == InboundPacketLogin.LOGIN_START) {
            this.sendEncryptionRequestPacket();
        }
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