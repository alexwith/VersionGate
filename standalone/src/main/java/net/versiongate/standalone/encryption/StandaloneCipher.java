package net.versiongate.standalone.encryption;

import io.netty.buffer.ByteBuf;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

public class StandaloneCipher {
    private final Cipher cipher;

    private boolean disposed;

    public StandaloneCipher(boolean encrypt, SecretKey key) throws GeneralSecurityException {
        this.cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        this.cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));
    }

    public void process(ByteBuf source) {
        if (this.disposed) {
            return;
        }

        final int inBytes = source.readableBytes();
        final int baseOffset = source.arrayOffset() + source.readerIndex();

        try {
            this.cipher.update(source.array(), baseOffset, inBytes, source.array(), baseOffset);
        } catch (ShortBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        this.disposed = true;
    }
}
