/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.prng;

import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import java.util.Map;

public abstract class BasePRNG
implements IRandom {
    protected String name;
    protected boolean initialised;
    protected byte[] buffer;
    protected int ndx;

    public String name() {
        return this.name;
    }

    public void init(Map attributes) {
        this.setup(attributes);
        this.ndx = 0;
        this.initialised = true;
    }

    public byte nextByte() throws IllegalStateException, LimitReachedException {
        if (!this.initialised) {
            throw new IllegalStateException();
        }
        return this.nextByteInternal();
    }

    public void nextBytes(byte[] out, int offset, int length) throws IllegalStateException, LimitReachedException {
        if (out == null) {
            return;
        }
        if (!this.initialised) {
            throw new IllegalStateException();
        }
        if (offset < 0 || offset >= out.length || length < 1) {
            return;
        }
        int limit = offset + length > out.length ? out.length - offset : length;
        int i = 0;
        while (i < limit) {
            out[offset++] = this.nextByteInternal();
            ++i;
        }
    }

    public boolean isInitialised() {
        return this.initialised;
    }

    private final byte nextByteInternal() throws LimitReachedException {
        if (this.ndx >= this.buffer.length) {
            this.fillBlock();
            this.ndx = 0;
        }
        return this.buffer[this.ndx++];
    }

    public abstract Object clone();

    public abstract void setup(Map var1);

    public abstract void fillBlock() throws LimitReachedException;

    protected BasePRNG(String name) {
        this.name = name;
        this.initialised = false;
        this.buffer = new byte[0];
    }
}

