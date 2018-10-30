/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.cipher;

import gnu.crypto.cipher.BaseCipher;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class NullCipher
extends BaseCipher {
    public final Object clone() {
        NullCipher result = new NullCipher();
        result.currentBlockSize = this.currentBlockSize;
        return result;
    }

    public final Iterator blockSizes() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(new Integer(8));
        al.add(new Integer(16));
        al.add(new Integer(24));
        al.add(new Integer(32));
        return Collections.unmodifiableList(al).iterator();
    }

    public final Iterator keySizes() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        int n = 8;
        while (n < 64) {
            al.add(new Integer(n));
            ++n;
        }
        return Collections.unmodifiableList(al).iterator();
    }

    public final Object makeKey(byte[] uk, int bs) throws InvalidKeyException {
        return new Object();
    }

    public final void encrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        System.arraycopy(in, i, out, j, bs);
    }

    public final void decrypt(byte[] in, int i, byte[] out, int j, Object k, int bs) {
        System.arraycopy(in, i, out, j, bs);
    }

    public final boolean selfTest() {
        return true;
    }

    public NullCipher() {
        super("null", 16, 16);
    }
}

