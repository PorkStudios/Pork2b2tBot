/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.cipher;

import gnu.crypto.cipher.BaseCipher;
import gnu.crypto.cipher.DES;
import gnu.crypto.cipher.TripleDES;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.Iterator;

public class TripleDES
extends BaseCipher {
    public static final int BLOCK_SIZE = 8;
    public static final int KEY_SIZE = 24;
    private DES des = new DES();

    public static void adjustParity(byte[] kb, int offset) {
        DES.adjustParity(kb, offset);
        DES.adjustParity(kb, offset + 8);
        DES.adjustParity(kb, offset + 16);
    }

    public static boolean isParityAdjusted(byte[] kb, int offset) {
        boolean bl = false;
        if (DES.isParityAdjusted(kb, offset) && DES.isParityAdjusted(kb, offset + 8) && DES.isParityAdjusted(kb, offset + 16)) {
            bl = true;
        }
        return bl;
    }

    public Object clone() {
        return new TripleDES();
    }

    public Iterator blockSizes() {
        return Collections.singleton(new Integer(8)).iterator();
    }

    public Iterator keySizes() {
        return Collections.singleton(new Integer(24)).iterator();
    }

    public Object makeKey(byte[] kb, int bs) throws InvalidKeyException {
        if (kb.length != 24) {
            throw new InvalidKeyException("TripleDES key must be 24 bytes");
        }
        if (!TripleDES.isParityAdjusted(kb, 0)) {
            TripleDES.adjustParity(kb, 0);
        }
        byte[] k1 = new byte[8];
        byte[] k2 = new byte[8];
        byte[] k3 = new byte[8];
        System.arraycopy(kb, 0, k1, 0, 8);
        System.arraycopy(kb, 8, k2, 0, 8);
        System.arraycopy(kb, 16, k3, 0, 8);
        Context ctx = new Context();
        ctx.k1 = (DES.Context)this.des.makeKey(k1, bs);
        ctx.k2 = (DES.Context)this.des.makeKey(k2, bs);
        ctx.k3 = (DES.Context)this.des.makeKey(k3, bs);
        return ctx;
    }

    public void encrypt(byte[] in, int i, byte[] out, int o, Object K, int bs) {
        byte[] temp = new byte[8];
        this.des.encrypt(in, i, temp, 0, ((Context)K).k1, bs);
        this.des.decrypt(temp, 0, temp, 0, ((Context)K).k2, bs);
        this.des.encrypt(temp, 0, out, o, ((Context)K).k3, bs);
    }

    public void decrypt(byte[] in, int i, byte[] out, int o, Object K, int bs) {
        byte[] temp = new byte[8];
        this.des.decrypt(in, i, temp, 0, ((Context)K).k3, bs);
        this.des.encrypt(temp, 0, temp, 0, ((Context)K).k2, bs);
        this.des.decrypt(temp, 0, out, o, ((Context)K).k1, bs);
    }

    public TripleDES() {
        super("tripledes", 8, 24);
    }

    private final class Context {
        DES.Context k1;
        DES.Context k2;
        DES.Context k3;

        private Context() {
        }
    }

}

