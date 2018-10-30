/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.UMacGenerator;
import gnu.crypto.util.PRNG;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class KDF {
    private static final int AES_BLOCK_SIZE = 16;
    private static final int AES_KEY_SIZE = 16;
    private static final byte[] buffer = new byte[1];
    private UMacGenerator umac;

    static final KDF getInstance(byte[] K) {
        byte[] keyMaterial;
        int ndx = -1;
        if (K != null) {
            keyMaterial = K;
            ndx = 0;
        } else {
            keyMaterial = new byte[16];
            while (ndx < 1 || ndx > 255) {
                ndx = (byte)KDF.nextByte();
            }
        }
        return new KDF(keyMaterial, ndx);
    }

    private static final synchronized int nextByte() {
        PRNG.nextBytes(buffer);
        return buffer[0] & 255;
    }

    public synchronized byte[] derive(int length) {
        byte[] result = new byte[length];
        try {
            this.umac.nextBytes(result, 0, length);
        }
        catch (IllegalStateException x) {
            x.printStackTrace(System.err);
        }
        catch (LimitReachedException x) {
            x.printStackTrace(System.err);
        }
        return result;
    }

    private final /* synthetic */ void this() {
        this.umac = null;
    }

    private KDF(byte[] keyMaterial, int ndx) {
        this.this();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("gnu.crypto.prng.umac.cipher.name", "aes");
        map.put("gnu.crypto.prng.umac.index", new Integer(ndx));
        map.put("gnu.crypto.cipher.block.size", new Integer(16));
        byte[] key = new byte[16];
        System.arraycopy(keyMaterial, 0, key, 0, 16);
        map.put("gnu.crypto.cipher.key.material", key);
        this.umac = new UMacGenerator();
        this.umac.init(map);
    }
}

