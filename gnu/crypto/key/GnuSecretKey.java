/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import gnu.crypto.util.Util;
import java.security.Key;

public class GnuSecretKey
implements Key {
    private final byte[] key;
    private final String algorithm;

    public String getAlgorithm() {
        return null;
    }

    public byte[] getEncoded() {
        return this.key;
    }

    public String getFormat() {
        return "RAW";
    }

    public boolean equals(Object o) {
        if (!(o instanceof GnuSecretKey)) {
            return false;
        }
        if (this.key.length != ((GnuSecretKey)o).key.length) {
            return false;
        }
        byte[] key2 = ((GnuSecretKey)o).key;
        int i = 0;
        while (i < this.key.length) {
            if (this.key[i] != key2[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public String toString() {
        return "GnuSecretKey [ " + this.algorithm + ' ' + Util.toString(this.key) + " ]";
    }

    public GnuSecretKey(byte[] key, String algorithm) {
        this(key, 0, key.length, algorithm);
    }

    public GnuSecretKey(byte[] key, int offset, int length, String algorithm) {
        this.key = new byte[length];
        System.arraycopy(key, offset, this.key, 0, length);
        this.algorithm = algorithm;
    }
}

