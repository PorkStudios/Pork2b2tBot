/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mode;

import gnu.crypto.Registry;
import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mode.BaseMode;
import gnu.crypto.mode.CBC;
import gnu.crypto.mode.CFB;
import gnu.crypto.mode.CTR;
import gnu.crypto.mode.ECB;
import gnu.crypto.mode.ICM;
import gnu.crypto.mode.IMode;
import gnu.crypto.mode.OFB;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ModeFactory
implements Registry {
    public static IMode getInstance(String mode, String cipher, int cipherBlockSize) {
        if (mode == null || cipher == null) {
            return null;
        }
        mode = mode.trim();
        IBlockCipher cipherImpl = CipherFactory.getInstance(cipher = cipher.trim());
        if (cipherImpl == null) {
            return null;
        }
        return ModeFactory.getInstance(mode, cipherImpl, cipherBlockSize);
    }

    public static IMode getInstance(String mode, IBlockCipher cipher, int cipherBlockSize) {
        boolean ok = false;
        Iterator it = cipher.blockSizes();
        while (it.hasNext()) {
            boolean bl = false;
            if (cipherBlockSize == (Integer)it.next()) {
                bl = true;
            }
            if (ok = bl) break;
        }
        if (!ok) {
            throw new IllegalArgumentException("cipherBlockSize");
        }
        BaseMode result = null;
        if (mode.equalsIgnoreCase("ecb")) {
            result = new ECB(cipher, cipherBlockSize);
        } else if (mode.equalsIgnoreCase("ctr")) {
            result = new CTR(cipher, cipherBlockSize);
        } else if (mode.equalsIgnoreCase("icm")) {
            result = new ICM(cipher, cipherBlockSize);
        } else if (mode.equalsIgnoreCase("ofb")) {
            result = new OFB(cipher, cipherBlockSize);
        } else if (mode.equalsIgnoreCase("cbc")) {
            result = new CBC(cipher, cipherBlockSize);
        } else if (mode.equalsIgnoreCase("cfb")) {
            result = new CFB(cipher, cipherBlockSize);
        }
        if (result != null && !result.selfTest()) {
            throw new InternalError(result.name());
        }
        return result;
    }

    public static final Set getNames() {
        HashSet<String> hs = new HashSet<String>();
        hs.add("ecb");
        hs.add("ctr");
        hs.add("icm");
        hs.add("ofb");
        hs.add("cbc");
        hs.add("cfb");
        return Collections.unmodifiableSet(hs);
    }

    private ModeFactory() {
    }
}

