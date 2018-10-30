/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import gnu.crypto.key.IKeyAgreementParty;
import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.util.PRNG;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public abstract class BaseKeyAgreementParty
implements IKeyAgreementParty {
    protected static final BigInteger TWO = BigInteger.valueOf(2);
    protected String name;
    protected boolean initialised;
    protected int step;
    protected boolean complete;
    protected SecureRandom rnd;
    protected IRandom irnd;

    public String name() {
        return this.name;
    }

    public void init(Map attributes) throws KeyAgreementException {
        if (this.initialised) {
            throw new IllegalStateException("already initialised");
        }
        this.engineInit(attributes);
        this.initialised = true;
        this.step = -1;
        this.complete = false;
    }

    public OutgoingMessage processMessage(IncomingMessage in) throws KeyAgreementException {
        if (!this.initialised) {
            throw new IllegalStateException("not initialised");
        }
        if (this.complete) {
            throw new IllegalStateException("exchange has already concluded");
        }
        ++this.step;
        return this.engineProcessMessage(in);
    }

    public boolean isComplete() {
        return this.complete;
    }

    public byte[] getSharedSecret() throws KeyAgreementException {
        if (!this.initialised) {
            throw new KeyAgreementException("not yet initialised");
        }
        if (!this.isComplete()) {
            throw new KeyAgreementException("not yet computed");
        }
        return this.engineSharedSecret();
    }

    public void reset() {
        if (this.initialised) {
            this.engineReset();
            this.initialised = false;
        }
    }

    protected abstract void engineInit(Map var1) throws KeyAgreementException;

    protected abstract OutgoingMessage engineProcessMessage(IncomingMessage var1) throws KeyAgreementException;

    protected abstract byte[] engineSharedSecret() throws KeyAgreementException;

    protected abstract void engineReset();

    protected void nextRandomBytes(byte[] buffer) {
        if (this.rnd != null) {
            this.rnd.nextBytes(buffer);
        } else if (this.irnd != null) {
            try {
                this.irnd.nextBytes(buffer, 0, buffer.length);
            }
            catch (LimitReachedException lre) {
                this.irnd = null;
                PRNG.nextBytes(buffer);
            }
        } else {
            PRNG.nextBytes(buffer);
        }
    }

    private final /* synthetic */ void this() {
        this.initialised = false;
        this.step = -1;
        this.complete = false;
        this.rnd = null;
        this.irnd = null;
    }

    protected BaseKeyAgreementParty(String name) {
        this.this();
        this.name = name;
    }
}

