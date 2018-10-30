/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig.rsa;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;

public class EMSA_PSS
implements Cloneable {
    private static final String NAME = "emsa-pss";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 5;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private IMessageDigest hash;
    private int hLen;

    private static final void debug(String s) {
        err.println(">>> emsa-pss: " + s);
    }

    public static EMSA_PSS getInstance(String mdName) {
        IMessageDigest hash = HashFactory.getInstance(mdName);
        return new EMSA_PSS(hash);
    }

    public Object clone() {
        return EMSA_PSS.getInstance(this.hash.name());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public byte[] encode(byte[] mHash, int emBits, byte[] salt) {
        int i;
        byte[] H;
        int sLen = salt.length;
        if (this.hLen != mHash.length) {
            throw new IllegalArgumentException("wrong hash");
        }
        if (emBits < 8 * this.hLen + 8 * sLen + 9) {
            throw new IllegalArgumentException("encoding error");
        }
        int emLen = (emBits + 7) / 8;
        IMessageDigest iMessageDigest = this.hash;
        synchronized (iMessageDigest) {
            i = 0;
            do {
                if (i >= 8) {
                    this.hash.update(mHash, 0, this.hLen);
                    this.hash.update(salt, 0, sLen);
                    H = this.hash.digest();
                    break;
                }
                this.hash.update((byte)0);
                ++i;
            } while (true);
        }
        byte[] DB = new byte[emLen - sLen - this.hLen - 2 + 1 + sLen];
        DB[emLen - sLen - this.hLen - 2] = 1;
        System.arraycopy(salt, 0, DB, emLen - sLen - this.hLen - 1, sLen);
        byte[] dbMask = this.MGF(H, emLen - this.hLen - 1);
        i = 0;
        while (i < DB.length) {
            DB[i] = (byte)(DB[i] ^ dbMask[i]);
            ++i;
        }
        byte[] arrby = DB;
        arrby[0] = (byte)(arrby[0] & 255 >>> 8 * emLen - emBits);
        byte[] result = new byte[emLen];
        System.arraycopy(DB, 0, result, 0, emLen - this.hLen - 1);
        System.arraycopy(H, 0, result, emLen - this.hLen - 1, this.hLen);
        result[emLen - 1] = -68;
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean decode(byte[] mHash, byte[] EM, int emBits, int sLen) {
        if (sLen < 0) {
            throw new IllegalArgumentException("sLen");
        }
        if (this.hLen != mHash.length) {
            throw new IllegalArgumentException("wrong hash");
        }
        if (emBits < 8 * this.hLen + 8 * sLen + 9) {
            throw new IllegalArgumentException("decoding error");
        }
        int emLen = (emBits + 7) / 8;
        if ((EM[EM.length - 1] & 255) != 188) {
            return false;
        }
        if ((EM[0] & 255 << 8 - (8 * emLen - emBits)) != 0) {
            return false;
        }
        byte[] DB = new byte[emLen - this.hLen - 1];
        byte[] H = new byte[this.hLen];
        System.arraycopy(EM, 0, DB, 0, emLen - this.hLen - 1);
        System.arraycopy(EM, emLen - this.hLen - 1, H, 0, this.hLen);
        byte[] dbMask = this.MGF(H, emLen - this.hLen - 1);
        int i = 0;
        while (i < DB.length) {
            DB[i] = (byte)(DB[i] ^ dbMask[i]);
            ++i;
        }
        byte[] arrby = DB;
        arrby[0] = (byte)(arrby[0] & 255 >>> 8 * emLen - emBits);
        i = 0;
        while (i < emLen - this.hLen - sLen - 2) {
            if (DB[i] != 0) {
                return false;
            }
            ++i;
        }
        if (DB[i] != 1) {
            return false;
        }
        byte[] salt = new byte[sLen];
        System.arraycopy(DB, DB.length - sLen, salt, 0, sLen);
        IMessageDigest iMessageDigest = this.hash;
        synchronized (iMessageDigest) {
            i = 0;
            do {
                if (i >= 8) {
                    this.hash.update(mHash, 0, this.hLen);
                    this.hash.update(salt, 0, sLen);
                    byte[] H0 = this.hash.digest();
                    return Arrays.equals(H, H0);
                }
                this.hash.update((byte)0);
                ++i;
            } while (true);
        }
    }

    private final byte[] MGF(byte[] Z, int l) {
        if (l < 1 || ((long)l & 0xFFFFFFFFL) > ((long)this.hLen & 0xFFFFFFFFL) << 32) {
            throw new IllegalArgumentException("mask too long");
        }
        byte[] result = new byte[l];
        int limit = (l + this.hLen - 1) / this.hLen - 1;
        IMessageDigest hashZ = null;
        hashZ = (IMessageDigest)this.hash.clone();
        hashZ.digest();
        hashZ.update(Z, 0, Z.length);
        IMessageDigest hashZC = null;
        int sofar = 0;
        int i = 0;
        while (i < limit) {
            hashZC = (IMessageDigest)hashZ.clone();
            hashZC.update((byte)(i >>> 24));
            hashZC.update((byte)(i >>> 16));
            hashZC.update((byte)(i >>> 8));
            hashZC.update((byte)i);
            byte[] t = hashZC.digest();
            int length = l - sofar;
            length = length > this.hLen ? this.hLen : length;
            System.arraycopy(t, 0, result, sofar, length);
            sofar += length;
            ++i;
        }
        return result;
    }

    private EMSA_PSS(IMessageDigest hash) {
        this.hash = hash;
        this.hLen = hash.hashSize();
    }
}

