/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import gnu.crypto.key.IKeyPairCodec;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.KeyPairCodecFactory;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

public class IncomingMessage {
    protected ByteArrayInputStream in;
    protected int length;

    public static IncomingMessage getInstance(byte[] raw) {
        return IncomingMessage.getInstance(raw, 0, raw.length);
    }

    public static IncomingMessage getInstance(byte[] raw, int offset, int len) {
        IncomingMessage result = new IncomingMessage();
        result.in = new ByteArrayInputStream(raw, offset, len);
        return result;
    }

    public static int twoBytesToLength(byte[] b) throws KeyAgreementException {
        int result = (b[0] & 255) << 8 | b[1] & 255;
        if (result > (char)-1) {
            throw new KeyAgreementException("encoded MPI size limit exceeded");
        }
        return result;
    }

    public static int fourBytesToLength(byte[] b) throws KeyAgreementException {
        int result = b[0] << 24 | (b[1] & 255) << 16 | (b[2] & 255) << 8 | b[3] & 255;
        if (result > 2147483383 || result < 0) {
            throw new KeyAgreementException("encoded entity size limit exceeded");
        }
        return result;
    }

    public boolean hasMoreElements() {
        boolean bl = false;
        if (this.in.available() > 0) {
            bl = true;
        }
        return bl;
    }

    public PublicKey readPublicKey() throws KeyAgreementException {
        if (this.in.available() < 4) {
            throw new KeyAgreementException("not enough bytes for a public key in message");
        }
        byte[] elementLengthBytes = new byte[4];
        this.in.read(elementLengthBytes, 0, 4);
        int elementLength = IncomingMessage.fourBytesToLength(elementLengthBytes);
        if (this.in.available() < elementLength) {
            throw new KeyAgreementException("illegal public key encoding");
        }
        byte[] kb = new byte[elementLength];
        this.in.read(kb, 0, elementLength);
        IKeyPairCodec kpc = KeyPairCodecFactory.getInstance(kb);
        if (kpc == null) {
            throw new KeyAgreementException("invalid public key, or encoded with an unknown codec");
        }
        return kpc.decodePublicKey(kb);
    }

    public PrivateKey readPrivateKey() throws KeyAgreementException {
        if (this.in.available() < 4) {
            throw new KeyAgreementException("not enough bytes for a private key in message");
        }
        byte[] elementLengthBytes = new byte[4];
        this.in.read(elementLengthBytes, 0, 4);
        int elementLength = IncomingMessage.fourBytesToLength(elementLengthBytes);
        if (this.in.available() < elementLength) {
            throw new KeyAgreementException("illegal private key encoding");
        }
        byte[] kb = new byte[elementLength];
        this.in.read(kb, 0, elementLength);
        IKeyPairCodec kpc = KeyPairCodecFactory.getInstance(kb);
        if (kpc == null) {
            throw new KeyAgreementException("invalid private key, or encoded with an unknown codec");
        }
        return kpc.decodePrivateKey(kb);
    }

    public BigInteger readMPI() throws KeyAgreementException {
        if (this.in.available() < 2) {
            throw new KeyAgreementException("not enough bytes for an MPI in message");
        }
        byte[] elementLengthBytes = new byte[2];
        this.in.read(elementLengthBytes, 0, 2);
        int elementLength = IncomingMessage.twoBytesToLength(elementLengthBytes);
        if (this.in.available() < elementLength) {
            throw new KeyAgreementException("illegal MPI encoding");
        }
        byte[] element = new byte[elementLength];
        this.in.read(element, 0, element.length);
        return new BigInteger(1, element);
    }

    public String readString() throws KeyAgreementException {
        if (this.in.available() < 2) {
            throw new KeyAgreementException("not enough bytes for a text in message");
        }
        byte[] elementLengthBytes = new byte[2];
        this.in.read(elementLengthBytes, 0, 2);
        int elementLength = IncomingMessage.twoBytesToLength(elementLengthBytes);
        if (this.in.available() < elementLength) {
            throw new KeyAgreementException("illegal text encoding");
        }
        byte[] element = new byte[elementLength];
        this.in.read(element, 0, element.length);
        String result = null;
        try {
            result = new String(element, "UTF8");
        }
        catch (UnsupportedEncodingException x) {
            throw new KeyAgreementException("unxupported UTF8 encoding", x);
        }
        return result;
    }

    public IncomingMessage(byte[] b) throws KeyAgreementException {
        this();
        if (b.length < 4) {
            throw new KeyAgreementException("message header too short");
        }
        this.length = b[0] << 24 | (b[1] & 255) << 16 | (b[2] & 255) << 8 | b[3] & 255;
        if (this.length > 2147483643 || this.length < 0) {
            throw new KeyAgreementException("message size limit exceeded");
        }
        this.in = new ByteArrayInputStream(b, 4, this.length);
    }

    private IncomingMessage() {
    }
}

