/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.cipher;

import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.PRNGFactory;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

public class ARCFourSpi
extends CipherSpi {
    private IRandom keystream = PRNGFactory.getInstance("arcfour");

    protected int engineGetBlockSize() {
        return 0;
    }

    protected void engineSetMode(String s) throws NoSuchAlgorithmException {
    }

    protected void engineSetPadding(String s) throws NoSuchPaddingException {
    }

    protected byte[] engineGetIV() {
        return null;
    }

    protected int engineGetOutputSize(int in) {
        return in;
    }

    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    protected void engineInit(int mode, Key key, SecureRandom r) throws InvalidKeyException {
        if (mode != 1 && mode != 2) {
            throw new IllegalArgumentException("arcfour is for encryption or decryption only");
        }
        if (key == null || !key.getFormat().equalsIgnoreCase("RAW")) {
            throw new InvalidKeyException("key must be non-null raw bytes");
        }
        HashMap<String, byte[]> attrib = new HashMap<String, byte[]>();
        attrib.put("gnu.crypto.prng.arcfour.key-material", key.getEncoded());
        this.keystream.init(attrib);
    }

    protected void engineInit(int mode, Key key, AlgorithmParameterSpec p, SecureRandom r) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.engineInit(mode, key, r);
    }

    protected void engineInit(int mode, Key key, AlgorithmParameters p, SecureRandom r) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.engineInit(mode, key, r);
    }

    protected byte[] engineUpdate(byte[] in, int offset, int length) {
        byte[] result;
        if (length < 0 || offset < 0 || length + offset > in.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        result = new byte[length];
        try {
            int i = 0;
            while (i < length) {
                result[i] = (byte)(in[i + offset] ^ this.keystream.nextByte());
                ++i;
            }
        }
        catch (LimitReachedException limitReachedException) {}
        return result;
    }

    protected int engineUpdate(byte[] in, int inOffset, int length, byte[] out, int outOffset) throws ShortBufferException {
        if (length < 0 || inOffset < 0 || length + inOffset > in.length || outOffset < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (outOffset + length > out.length) {
            throw new ShortBufferException();
        }
        try {
            int i = 0;
            while (i < length) {
                out[i + outOffset] = (byte)(in[i + inOffset] ^ this.keystream.nextByte());
                ++i;
            }
        }
        catch (LimitReachedException limitReachedException) {}
        return length;
    }

    protected byte[] engineDoFinal(byte[] in, int offset, int length) throws IllegalBlockSizeException, BadPaddingException {
        return this.engineUpdate(in, offset, length);
    }

    protected int engineDoFinal(byte[] in, int inOffset, int length, byte[] out, int outOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.engineUpdate(in, inOffset, length, out, outOffset);
    }
}

