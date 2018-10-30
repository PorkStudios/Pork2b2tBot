/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import java.security.KeyPair;
import java.util.Map;

public interface IKeyPairGenerator {
    public String name();

    public void setup(Map var1);

    public KeyPair generate();
}

