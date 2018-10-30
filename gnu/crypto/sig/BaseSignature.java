/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig;

import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.sig.ISignature;
import gnu.crypto.util.PRNG;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.Random;

public abstract class BaseSignature
implements ISignature {
    protected String schemeName;
    protected IMessageDigest md;
    protected PublicKey publicKey;
    protected PrivateKey privateKey;
    private Random rnd;
    private IRandom irnd;

    public String name() {
        return this.schemeName;
    }

    public void setupVerify(Map attributes) throws IllegalArgumentException {
        this.setup(attributes);
        PublicKey key = (PublicKey)attributes.get("gnu.crypto.sig.public.key");
        if (key != null) {
            this.setupForVerification(key);
        }
    }

    public void setupSign(Map attributes) throws IllegalArgumentException {
        this.setup(attributes);
        PrivateKey key = (PrivateKey)attributes.get("gnu.crypto.sig.private.key");
        if (key != null) {
            this.setupForSigning(key);
        }
    }

    public void update(byte b) {
        if (this.md == null) {
            throw new IllegalStateException();
        }
        this.md.update(b);
    }

    public void update(byte[] b, int off, int len) {
        if (this.md == null) {
            throw new IllegalStateException();
        }
        this.md.update(b, off, len);
    }

    public Object sign() {
        if (this.md == null || this.privateKey == null) {
            throw new IllegalStateException();
        }
        return this.generateSignature();
    }

    public boolean verify(Object sig) {
        if (this.md == null || this.publicKey == null) {
            throw new IllegalStateException();
        }
        return this.verifySignature(sig);
    }

    public abstract Object clone();

    protected abstract void setupForVerification(PublicKey var1) throws IllegalArgumentException;

    protected abstract void setupForSigning(PrivateKey var1) throws IllegalArgumentException;

    protected abstract Object generateSignature() throws IllegalStateException;

    protected abstract boolean verifySignature(Object var1) throws IllegalStateException;

    protected void init() {
        this.md.reset();
        this.rnd = null;
        this.irnd = null;
        this.publicKey = null;
        this.privateKey = null;
    }

    protected void nextRandomBytes(byte[] buffer) {
        if (this.rnd != null) {
            this.rnd.nextBytes(buffer);
        } else if (this.irnd != null) {
            try {
                this.irnd.nextBytes(buffer, 0, buffer.length);
            }
            catch (IllegalStateException x) {
                throw new RuntimeException("nextRandomBytes(): " + String.valueOf(x));
            }
            catch (LimitReachedException x) {
                throw new RuntimeException("nextRandomBytes(): " + String.valueOf(x));
            }
        } else {
            PRNG.nextBytes(buffer);
        }
    }

    private final void setup(Map attributes) {
        this.init();
        V obj = attributes.get("gnu.crypto.sig.prng");
        if (obj instanceof Random) {
            this.rnd = (Random)obj;
        } else if (obj instanceof IRandom) {
            this.irnd = (IRandom)obj;
        }
    }

    protected BaseSignature(String schemeName, IMessageDigest md) {
        this.schemeName = schemeName;
        this.md = md;
    }
}

