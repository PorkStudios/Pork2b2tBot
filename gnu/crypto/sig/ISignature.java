/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig;

import java.util.Map;

public interface ISignature
extends Cloneable {
    public static final String VERIFIER_KEY = "gnu.crypto.sig.public.key";
    public static final String SIGNER_KEY = "gnu.crypto.sig.private.key";
    public static final String SOURCE_OF_RANDOMNESS = "gnu.crypto.sig.prng";

    public String name();

    public void setupVerify(Map var1) throws IllegalArgumentException;

    public void setupSign(Map var1) throws IllegalArgumentException;

    public void update(byte var1) throws IllegalStateException;

    public void update(byte[] var1, int var2, int var3) throws IllegalStateException;

    public Object sign() throws IllegalStateException;

    public boolean verify(Object var1) throws IllegalStateException;

    public Object clone();
}

