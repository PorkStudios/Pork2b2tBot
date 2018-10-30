/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.prng;

import gnu.crypto.prng.ICMGenerator;
import gnu.crypto.prng.LimitReachedException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandomSpi;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class ICMRandomSpi
extends SecureRandomSpi {
    private static final String NAME = "ICMRandomSpi";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 0;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private static final ICMGenerator prng = new ICMGenerator();
    private static final String MSG = "Exception while setting up an icm SPI: ";
    private static final String RETRY = "Retry...";
    private static final String LIMIT_REACHED_MSG = "Limit reached: ";
    private static final String RESEED = "Re-seed...";
    private ICMGenerator adaptee;

    private static final void debug(String s) {
        err.println(">>> ICMRandomSpi: " + s);
    }

    private static final void resetLocalPRNG() {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("gnu.crypto.prng.icm.cipher.name", "aes");
        byte[] key = new byte[16];
        Random rand = new Random(System.currentTimeMillis());
        rand.nextBytes(key);
        attributes.put("gnu.crypto.cipher.key.material", key);
        int aesBlockSize = 16;
        byte[] offset = new byte[aesBlockSize];
        rand.nextBytes(offset);
        attributes.put("gnu.crypto.prng.icm.offset", offset);
        int ndxLen = 0;
        int limit = aesBlockSize / 2;
        while (ndxLen < 1 || ndxLen > limit) {
            ndxLen = rand.nextInt(limit + 1);
        }
        attributes.put("gnu.crypto.prng.icm.segment.index.length", new Integer(ndxLen));
        byte[] index = new byte[ndxLen];
        rand.nextBytes(index);
        attributes.put("gnu.crypto.prng.icm.segment.index", new BigInteger(1, index));
        prng.setup(attributes);
    }

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
        do {
            try {
                this.adaptee.nextBytes(bytes, 0, bytes.length);
            }
            catch (LimitReachedException x) {
                ICMRandomSpi.resetLocalPRNG();
                continue;
            }
            break;
        } while (true);
    }

    public void engineSetSeed(byte[] seed) {
        int materialLength = 0;
        materialLength += 16;
        materialLength += 16;
        byte[] material = new byte[materialLength += 8];
        int materialOffset = 0;
        int materialLeft = material.length;
        if (seed.length > 0) {
            int lenToCopy = Math.min(materialLength, seed.length);
            System.arraycopy(seed, 0, material, 0, lenToCopy);
            materialOffset += lenToCopy;
            materialLeft -= lenToCopy;
        }
        if (materialOffset > 0) {
            do {
                try {
                    prng.nextBytes(material, materialOffset, materialLeft);
                }
                catch (IllegalStateException x) {
                    throw new InternalError(MSG + String.valueOf(x));
                }
                catch (LimitReachedException limitReachedException) {
                    continue;
                }
                break;
            } while (true);
        }
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("gnu.crypto.prng.icm.cipher.name", "aes");
        attributes.put("gnu.crypto.prng.icm.segment.index.length", new Integer(4));
        byte[] key = new byte[16];
        System.arraycopy(material, 0, key, 0, 16);
        attributes.put("gnu.crypto.cipher.key.material", key);
        byte[] offset = new byte[16];
        System.arraycopy(material, 16, offset, 0, 16);
        attributes.put("gnu.crypto.prng.icm.offset", offset);
        byte[] index = new byte[8];
        System.arraycopy(material, 32, index, 0, 8);
        attributes.put("gnu.crypto.prng.icm.segment.index", new BigInteger(1, index));
        this.adaptee.init(attributes);
    }

    private final /* synthetic */ void this() {
        this.adaptee = new ICMGenerator();
    }

    public ICMRandomSpi() {
        this.this();
    }

    private static final {
        ICMRandomSpi.resetLocalPRNG();
    }
}

