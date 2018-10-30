/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.mac.IMac;
import java.security.InvalidKeyException;
import java.util.Map;

public abstract class BaseMac
implements IMac {
    protected String name;
    protected IMessageDigest underlyingHash;
    protected int truncatedSize;

    public String name() {
        return this.name;
    }

    public int macSize() {
        return this.truncatedSize;
    }

    public void update(byte b) {
        this.underlyingHash.update(b);
    }

    public void update(byte[] b, int offset, int len) {
        this.underlyingHash.update(b, offset, len);
    }

    public void reset() {
        this.underlyingHash.reset();
    }

    public abstract Object clone();

    public abstract void init(Map var1) throws InvalidKeyException, IllegalStateException;

    public abstract byte[] digest();

    public abstract boolean selfTest();

    protected BaseMac(String name) {
        this.name = name;
    }

    protected BaseMac(String name, IMessageDigest underlyingHash) {
        this(name);
        if (underlyingHash != null) {
            this.truncatedSize = underlyingHash.hashSize();
        }
        this.underlyingHash = underlyingHash;
    }
}

