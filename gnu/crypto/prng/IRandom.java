/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.prng;

import gnu.crypto.prng.LimitReachedException;
import java.util.Map;

public interface IRandom
extends Cloneable {
    public String name();

    public void init(Map var1);

    public byte nextByte() throws IllegalStateException, LimitReachedException;

    public void nextBytes(byte[] var1, int var2, int var3) throws IllegalStateException, LimitReachedException;

    public Object clone();
}

