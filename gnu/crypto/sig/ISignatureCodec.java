/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig;

public interface ISignatureCodec {
    public static final int RAW_FORMAT = 1;

    public int getFormatID();

    public byte[] encodeSignature(Object var1);

    public Object decodeSignature(byte[] var1);
}

