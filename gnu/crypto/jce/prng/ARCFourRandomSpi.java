/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.prng;

import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.PRNGFactory;
import java.security.SecureRandomSpi;
import java.util.HashMap;
import java.util.Map;

public class ARCFourRandomSpi
extends SecureRandomSpi {
    private IRandom adaptee = PRNGFactory.getInstance("arcfour");
    private boolean virgin = true;

    public byte[] engineGenerateSeed(int numBytes) {
        if (numBytes < 1) {
            return new byte[0];
        }
        byte[] result = new byte[numBytes];
        this.engineNextBytes(result);
        return result;
    }

    public void engineNextBytes(byte[] bytes) {
        if (this.virgin) {
            this.engineSetSeed(new byte[0]);
        }
        try {
            this.adaptee.nextBytes(bytes, 0, bytes.length);
        }
        catch (LimitReachedException limitReachedException) {}
    }

    public void engineSetSeed(byte[] seed) {
        HashMap<String, byte[]> attributes = new HashMap<String, byte[]>();
        attributes.put("gnu.crypto.prng.arcfour.key-material", seed);
        this.adaptee.init(attributes);
        this.virgin = false;
    }
}

