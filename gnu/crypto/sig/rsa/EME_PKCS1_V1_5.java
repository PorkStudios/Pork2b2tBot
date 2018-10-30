/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig.rsa;

import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.util.PRNG;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAKey;
import java.util.Random;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class EME_PKCS1_V1_5 {
    private int k;
    private ByteArrayOutputStream baos;

    public static final EME_PKCS1_V1_5 getInstance(int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k must be a positive integer");
        }
        return new EME_PKCS1_V1_5(k);
    }

    public static final EME_PKCS1_V1_5 getInstance(RSAKey key) {
        int modBits = key.getModulus().bitLength();
        int k = (modBits + 7) / 8;
        return EME_PKCS1_V1_5.getInstance(k);
    }

    public byte[] encode(byte[] M) {
        byte[] PS = new byte[this.k - M.length - 3];
        PRNG.nextBytes(PS);
        int i = 0;
        while (i < PS.length) {
            if (PS[i] == 0) {
                System.arraycopy(PS, i + 1, PS, i, PS.length - i - 1);
                PRNG.nextBytes(PS, PS.length - 1, 1);
                continue;
            }
            ++i;
        }
        return this.assembleEM(PS, M);
    }

    public byte[] encode(byte[] M, IRandom irnd) {
        byte[] PS = new byte[this.k - M.length - 3];
        try {
            irnd.nextBytes(PS, 0, PS.length);
            int i = 0;
            while (i < PS.length) {
                if (PS[i] == 0) {
                    System.arraycopy(PS, i + 1, PS, i, PS.length - i - 1);
                    irnd.nextBytes(PS, PS.length - 1, 1);
                    continue;
                }
                ++i;
            }
        }
        catch (IllegalStateException x) {
            throw new RuntimeException("encode(): " + String.valueOf(x));
        }
        catch (LimitReachedException x) {
            throw new RuntimeException("encode(): " + String.valueOf(x));
        }
        return this.assembleEM(PS, M);
    }

    public byte[] encode(byte[] M, Random rnd) {
        byte[] PS = new byte[this.k - M.length - 3];
        rnd.nextBytes(PS);
        int i = 0;
        while (i < PS.length) {
            if (PS[i] == 0) {
                System.arraycopy(PS, i + 1, PS, i, PS.length - i - 1);
                PS[PS.length - 1] = (byte)rnd.nextInt();
                continue;
            }
            ++i;
        }
        return this.assembleEM(PS, M);
    }

    public byte[] decode(byte[] EM) {
        int emLen = EM.length;
        if (emLen != this.k) {
            throw new IllegalArgumentException("decryption error");
        }
        if (EM[0] != 0) {
            throw new IllegalArgumentException("decryption error");
        }
        if (EM[1] != 2) {
            throw new IllegalArgumentException("decryption error");
        }
        int i = 2;
        while (i < emLen) {
            if (EM[i] == 0) break;
            ++i;
        }
        if (i >= emLen || i < 11) {
            throw new IllegalArgumentException("decryption error");
        }
        byte[] result = new byte[emLen - ++i];
        System.arraycopy(EM, i, result, 0, result.length);
        return result;
    }

    private final byte[] assembleEM(byte[] PS, byte[] M) {
        this.baos.reset();
        this.baos.write(0);
        this.baos.write(2);
        this.baos.write(PS, 0, PS.length);
        this.baos.write(0);
        this.baos.write(M, 0, M.length);
        byte[] result = this.baos.toByteArray();
        this.baos.reset();
        return result;
    }

    private final /* synthetic */ void this() {
        this.baos = new ByteArrayOutputStream();
    }

    private EME_PKCS1_V1_5(int k) {
        this.this();
        this.k = k;
    }
}

