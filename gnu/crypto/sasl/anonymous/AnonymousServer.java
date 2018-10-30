/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.anonymous;

import gnu.crypto.sasl.ServerMechanism;
import gnu.crypto.sasl.anonymous.AnonymousUtil;
import java.io.UnsupportedEncodingException;
import javax.security.sasl.AuthenticationException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

public class AnonymousServer
extends ServerMechanism
implements SaslServer {
    protected void initMechanism() throws SaslException {
    }

    protected void resetMechanism() throws SaslException {
    }

    public byte[] evaluateResponse(byte[] response) throws SaslException {
        if (response == null) {
            return null;
        }
        try {
            this.authorizationID = new String(response, "UTF-8");
        }
        catch (UnsupportedEncodingException x) {
            throw new AuthenticationException("evaluateResponse()", x);
        }
        if (AnonymousUtil.isValidTraceInformation(this.authorizationID)) {
            this.complete = true;
            return null;
        }
        this.authorizationID = null;
        throw new AuthenticationException("Invalid email address");
    }

    public AnonymousServer() {
        super("ANONYMOUS");
    }
}

