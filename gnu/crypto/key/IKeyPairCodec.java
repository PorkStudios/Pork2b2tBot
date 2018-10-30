/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface IKeyPairCodec {
    public static final int RAW_FORMAT = 1;

    public int getFormatID();

    public byte[] encodePublicKey(PublicKey var1);

    public byte[] encodePrivateKey(PrivateKey var1);

    public PublicKey decodePublicKey(byte[] var1);

    public PrivateKey decodePrivateKey(byte[] var1);
}

