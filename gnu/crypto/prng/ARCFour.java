/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.prng;

import gnu.crypto.prng.BasePRNG;
import gnu.crypto.prng.LimitReachedException;
import java.util.Map;

public class ARCFour
extends BasePRNG {
    public static final String ARCFOUR_KEY_MATERIAL = "gnu.crypto.prng.arcfour.key-material";
    public static final int ARCFOUR_SBOX_SIZE = 256;
    private byte[] s;
    private byte m;
    private byte n;

    public Object clone() {
        ARCFour copy = new ARCFour();
        copy.s = this.s != null ? (byte[])this.s.clone() : null;
        copy.m = this.m;
        copy.n = this.n;
        copy.buffer = this.buffer != null ? (byte[])this.buffer.clone() : null;
        copy.ndx = this.ndx;
        copy.initialised = this.initialised;
        return copy;
    }

    public void setup(Map attributes) {
        int j;
        byte[] kb = (byte[])attributes.get(ARCFOUR_KEY_MATERIAL);
        if (kb == null) {
            throw new IllegalArgumentException("ARCFOUR needs a key");
        }
        this.s = new byte[256];
        this.n = 0;
        this.m = 0;
        byte[] k = new byte[256];
        int i = 0;
        while (i < 256) {
            this.s[i] = (byte)i;
            ++i;
        }
        if (kb.length > 0) {
            i = 0;
            j = 0;
            while (i < 256) {
                k[i] = kb[j++];
                if (j >= kb.length) {
                    j = 0;
                }
                ++i;
            }
        }
        i = 0;
        j = 0;
        while (i < 256) {
            j = j + this.s[i] + k[i];
            byte temp = this.s[i];
            this.s[i] = this.s[j & 255];
            this.s[j & 255] = temp;
            ++i;
        }
        this.buffer = new byte[256];
        try {
            this.fillBlock();
        }
        catch (LimitReachedException limitReachedException) {}
    }

    public void fillBlock() throws LimitReachedException {
        int i = 0;
        while (i < this.buffer.length) {
            this.m = (byte)(this.m + 1);
            this.n = (byte)(this.n + this.s[this.m & 255]);
            byte temp = this.s[this.m & 255];
            this.s[this.m & 255] = this.s[this.n & 255];
            this.s[this.n & 255] = temp;
            temp = (byte)(this.s[this.m & 255] + this.s[this.n & 255]);
            this.buffer[i] = this.s[temp & 255];
            ++i;
        }
    }

    public ARCFour() {
        super("arcfour");
    }
}

