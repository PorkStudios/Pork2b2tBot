/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.prng;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.prng.BasePRNG;
import gnu.crypto.prng.LimitReachedException;
import java.util.Map;

public class MDGenerator
extends BasePRNG {
    public static final String MD_NAME = "gnu.crypto.prng.md.hash.name";
    public static final String SEEED = "gnu.crypto.prng.md.seed";
    private IMessageDigest md;

    public Object clone() {
        return new MDGenerator(this);
    }

    public void setup(Map attributes) {
        String underlyingMD = (String)attributes.get(MD_NAME);
        if (underlyingMD == null) {
            if (this.md == null) {
                this.md = HashFactory.getInstance("sha-160");
            } else {
                this.md.reset();
            }
        } else {
            this.md = HashFactory.getInstance(underlyingMD);
        }
        byte[] seed = (byte[])attributes.get(SEEED);
        if (seed == null) {
            seed = new byte[]{};
        }
        this.md.update(seed, 0, seed.length);
    }

    public void fillBlock() throws LimitReachedException {
        IMessageDigest mdc = (IMessageDigest)this.md.clone();
        this.buffer = mdc.digest();
        this.md.update(this.buffer, 0, this.buffer.length);
    }

    public MDGenerator() {
        super("md");
    }

    private MDGenerator(MDGenerator that) {
        this();
        this.md = that.md == null ? null : (IMessageDigest)that.md.clone();
        this.buffer = (byte[])that.buffer.clone();
        this.ndx = that.ndx;
        this.initialised = that.initialised;
    }
}

