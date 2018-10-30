/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.util;

import gnu.crypto.Properties;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.MDGenerator;
import java.util.HashMap;
import java.util.Map;

public class PRNG {
    private static final IRandom singleton = new MDGenerator();

    public static void nextBytes(byte[] buffer) {
        PRNG.nextBytes(buffer, 0, buffer.length);
    }

    public static void nextBytes(byte[] buffer, int offset, int length) {
        try {
            singleton.nextBytes(buffer, offset, length);
        }
        catch (LimitReachedException x) {
            try {
                HashMap<String, byte[]> map = new HashMap<String, byte[]>();
                if (!Properties.isReproducible()) {
                    long t = System.currentTimeMillis();
                    byte[] seed = new byte[]{(byte)(t >>> 56), (byte)(t >>> 48), (byte)(t >>> 40), (byte)(t >>> 32), (byte)(t >>> 24), (byte)(t >>> 16), (byte)(t >>> 8), (byte)t};
                    map.put("gnu.crypto.prng.md.seed", seed);
                }
                singleton.init(map);
                singleton.nextBytes(buffer, offset, length);
            }
            catch (Exception y) {
                throw new ExceptionInInitializerError(y);
            }
        }
    }

    private PRNG() {
    }

    private static final {
        try {
            HashMap<String, byte[]> map = new HashMap<String, byte[]>();
            if (!Properties.isReproducible()) {
                long t = System.currentTimeMillis();
                byte[] seed = new byte[]{(byte)(t >>> 56), (byte)(t >>> 48), (byte)(t >>> 40), (byte)(t >>> 32), (byte)(t >>> 24), (byte)(t >>> 16), (byte)(t >>> 8), (byte)t};
                map.put("gnu.crypto.prng.md.seed", seed);
            }
            singleton.init(map);
        }
        catch (Exception x) {
            throw new ExceptionInInitializerError(x);
        }
    }
}

