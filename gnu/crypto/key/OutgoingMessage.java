/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import gnu.crypto.key.IKeyPairCodec;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.KeyPairCodecFactory;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

public class OutgoingMessage {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public byte[] toByteArray() throws KeyAgreementException {
        byte[] buffer = this.wrap();
        int length = buffer.length;
        byte[] result = new byte[length + 4];
        result[0] = (byte)(length >>> 24);
        result[1] = (byte)(length >>> 16);
        result[2] = (byte)(length >>> 8);
        result[3] = (byte)length;
        System.arraycopy(buffer, 0, result, 4, length);
        return result;
    }

    public byte[] wrap() throws KeyAgreementException {
        int length = this.out.size();
        if (length > 2147483643 || length < 0) {
            throw new KeyAgreementException("message content is too long");
        }
        return this.out.toByteArray();
    }

    public void writePublicKey(PublicKey k) throws KeyAgreementException {
        IKeyPairCodec kpc = KeyPairCodecFactory.getInstance(k);
        if (kpc == null) {
            throw new KeyAgreementException("");
        }
        byte[] b = kpc.encodePublicKey(k);
        int length = b.length;
        if (length > 2147483383) {
            throw new KeyAgreementException("encoded public key is too long");
        }
        byte[] lengthBytes = new byte[]{(byte)(length >>> 24), (byte)(length >>> 16), (byte)(length >>> 8), (byte)length};
        this.out.write(lengthBytes, 0, 4);
        this.out.write(b, 0, b.length);
    }

    public void writePrivateKey(PrivateKey k) throws KeyAgreementException {
        IKeyPairCodec kpc = KeyPairCodecFactory.getInstance(k);
        if (kpc == null) {
            throw new KeyAgreementException("");
        }
        byte[] b = kpc.encodePrivateKey(k);
        int length = b.length;
        if (length > 2147483383) {
            throw new KeyAgreementException("encoded private key is too long");
        }
        byte[] lengthBytes = new byte[]{(byte)(length >>> 24), (byte)(length >>> 16), (byte)(length >>> 8), (byte)length};
        this.out.write(lengthBytes, 0, 4);
        this.out.write(b, 0, b.length);
    }

    public void writeMPI(BigInteger val) throws KeyAgreementException {
        byte[] b = val.toByteArray();
        int length = b.length;
        if (length > (char)-1) {
            throw new KeyAgreementException("MPI is too long");
        }
        byte[] lengthBytes = new byte[]{(byte)(length >>> 8), (byte)length};
        this.out.write(lengthBytes, 0, 2);
        this.out.write(b, 0, b.length);
    }

    public void writeString(String s) throws KeyAgreementException {
        byte[] b = null;
        try {
            b = s.getBytes("UTF8");
        }
        catch (UnsupportedEncodingException x) {
            throw new KeyAgreementException("unxupported UTF8 encoding", x);
        }
        int length = b.length;
        if (length > (char)-1) {
            throw new KeyAgreementException("text too long");
        }
        byte[] lengthBytes = new byte[]{(byte)(length >>> 8), (byte)length};
        this.out.write(lengthBytes, 0, 2);
        this.out.write(b, 0, b.length);
    }
}

