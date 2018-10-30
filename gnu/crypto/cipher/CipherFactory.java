/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.cipher;

import gnu.crypto.Registry;
import gnu.crypto.cipher.Anubis;
import gnu.crypto.cipher.BaseCipher;
import gnu.crypto.cipher.Blowfish;
import gnu.crypto.cipher.Cast5;
import gnu.crypto.cipher.DES;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.cipher.Khazad;
import gnu.crypto.cipher.NullCipher;
import gnu.crypto.cipher.Rijndael;
import gnu.crypto.cipher.Serpent;
import gnu.crypto.cipher.Square;
import gnu.crypto.cipher.TripleDES;
import gnu.crypto.cipher.Twofish;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CipherFactory
implements Registry {
    public static final IBlockCipher getInstance(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        BaseCipher result = null;
        if (name.equalsIgnoreCase("anubis")) {
            result = new Anubis();
        } else if (name.equalsIgnoreCase("blowfish")) {
            result = new Blowfish();
        } else if (name.equalsIgnoreCase("des")) {
            result = new DES();
        } else if (name.equalsIgnoreCase("khazad")) {
            result = new Khazad();
        } else if (name.equalsIgnoreCase("rijndael") || name.equalsIgnoreCase("aes")) {
            result = new Rijndael();
        } else if (name.equalsIgnoreCase("serpent")) {
            result = new Serpent();
        } else if (name.equalsIgnoreCase("square")) {
            result = new Square();
        } else if (name.equalsIgnoreCase("tripledes") || name.equalsIgnoreCase("desede")) {
            result = new TripleDES();
        } else if (name.equalsIgnoreCase("twofish")) {
            result = new Twofish();
        } else if (name.equalsIgnoreCase("cast5") || name.equalsIgnoreCase("cast128") || name.equalsIgnoreCase("cast-128")) {
            result = new Cast5();
        } else if (name.equalsIgnoreCase("null")) {
            result = new NullCipher();
        }
        if (result != null && !result.selfTest()) {
            throw new InternalError(result.name());
        }
        return result;
    }

    public static final Set getNames() {
        HashSet<String> hs = new HashSet<String>();
        hs.add("anubis");
        hs.add("blowfish");
        hs.add("des");
        hs.add("khazad");
        hs.add("rijndael");
        hs.add("serpent");
        hs.add("square");
        hs.add("tripledes");
        hs.add("twofish");
        hs.add("cast5");
        hs.add("null");
        return Collections.unmodifiableSet(hs);
    }

    private CipherFactory() {
    }
}

