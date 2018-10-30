/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.prng;

import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.MDGenerator;
import java.security.SecureRandomSpi;
import java.util.HashMap;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
abstract class SecureRandomAdapter
extends SecureRandomSpi {
    private MDGenerator adaptee;
    private String mdName;

    public byte[] engineGenerateSeed(int numBytes) {
        if (numBytes < 1) {
            return new byte[0];
        }
        byte[] result = new byte[numBytes];
        this.engineNextBytes(result);
        return result;
    }

    public void engineNextBytes(byte[] bytes) {
        if (!this.adaptee.isInitialised()) {
            this.engineSetSeed(new byte[0]);
        }
        try {
            this.adaptee.nextBytes(bytes, 0, bytes.length);
        }
        catch (LimitReachedException limitReachedException) {}
    }

    public void engineSetSeed(byte[] seed) {
        HashMap<String, String> attributes = new HashMap<String, String>();
        attributes.put("gnu.crypto.prng.md.hash.name", this.mdName);
        attributes.put("gnu.crypto.prng.md.seed", (String)seed);
        this.adaptee.init(attributes);
    }

    private final /* synthetic */ void this() {
        this.adaptee = new MDGenerator();
    }

    protected SecureRandomAdapter(String mdName) {
        this.this();
        this.mdName = mdName;
    }
}

