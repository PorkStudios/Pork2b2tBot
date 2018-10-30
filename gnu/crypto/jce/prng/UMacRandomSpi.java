/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.prng;

import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.UMacGenerator;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.SecureRandomSpi;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class UMacRandomSpi
extends SecureRandomSpi {
    private static final String NAME = "UMacRandomSpi";
    private static final boolean DEBUG = false;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private static final UMacGenerator prng = new UMacGenerator();
    private static final String MSG = "Exception while setting up a umac-kdf SPI: ";
    private static final String RETRY = "Retry...";
    private UMacGenerator adaptee;

    private static final void debug(String s) {
        err.println(">>> UMacRandomSpi: " + s);
    }

    private static final void resetLocalPRNG() {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("gnu.crypto.prng.umac.cipher.name", "aes");
        byte[] key = new byte[16];
        Random rand = new Random(System.currentTimeMillis());
        rand.nextBytes(key);
        attributes.put("gnu.crypto.cipher.key.material", key);
        int index = rand.nextInt() & 255;
        attributes.put("gnu.crypto.prng.umac.index", new Integer(index));
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
                UMacRandomSpi.resetLocalPRNG();
                continue;
            }
            break;
        } while (true);
    }

    public void engineSetSeed(byte[] seed) {
        int materialLength = 0;
        materialLength += 16;
        byte[] material = new byte[++materialLength];
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
        attributes.put("gnu.crypto.prng.umac.cipher.name", "aes");
        byte[] key = new byte[16];
        System.arraycopy(material, 0, key, 0, 16);
        attributes.put("gnu.crypto.cipher.key.material", key);
        attributes.put("gnu.crypto.prng.umac.index", new Integer(material[16] & 255));
        this.adaptee.init(attributes);
    }

    private final /* synthetic */ void this() {
        this.adaptee = new UMacGenerator();
    }

    public UMacRandomSpi() {
        this.this();
    }

    private static final {
        UMacRandomSpi.resetLocalPRNG();
    }
}

