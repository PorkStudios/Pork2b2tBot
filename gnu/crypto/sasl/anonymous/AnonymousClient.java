/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.anonymous;

import gnu.crypto.sasl.ClientMechanism;
import gnu.crypto.sasl.IllegalMechanismStateException;
import gnu.crypto.sasl.anonymous.AnonymousUtil;
import java.io.UnsupportedEncodingException;
import javax.security.sasl.AuthenticationException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class AnonymousClient
extends ClientMechanism
implements SaslClient {
    protected void initMechanism() throws SaslException {
    }

    protected void resetMechanism() throws SaslException {
    }

    public boolean hasInitialResponse() {
        return true;
    }

    public byte[] evaluateChallenge(byte[] challenge) throws SaslException {
        if (this.complete) {
            throw new IllegalMechanismStateException("evaluateChallenge()");
        }
        return this.response();
    }

    private final byte[] response() throws SaslException {
        byte[] result;
        if (!AnonymousUtil.isValidTraceInformation(this.authorizationID)) {
            throw new AuthenticationException("Authorisation ID is not a valid email address");
        }
        this.complete = true;
        try {
            result = this.authorizationID.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException x) {
            throw new AuthenticationException("response()", x);
        }
        return result;
    }

    public AnonymousClient() {
        super("ANONYMOUS");
    }
}

