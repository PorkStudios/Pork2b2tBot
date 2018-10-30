/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.plain;

import gnu.crypto.sasl.IAuthInfoProvider;
import gnu.crypto.sasl.NoSuchUserException;
import gnu.crypto.sasl.ServerMechanism;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

public class PlainServer
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
            byte[] password;
            byte[] pwd;
            String nullStr = new String("\u0000");
            StringTokenizer strtok = new StringTokenizer(new String(response), nullStr, true);
            this.authorizationID = strtok.nextToken();
            if (!this.authorizationID.equals(nullStr)) {
                strtok.nextToken();
            } else {
                this.authorizationID = null;
            }
            String id = strtok.nextToken();
            if (id.equals(nullStr)) {
                throw new SaslException("No identity given");
            }
            if (this.authorizationID == null) {
                this.authorizationID = id;
            }
            if (!this.authorizationID.equals(nullStr) && !this.authorizationID.equals(id)) {
                throw new SaslException("Delegation not supported");
            }
            strtok.nextToken();
            try {
                pwd = strtok.nextToken().getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException x) {
                throw new SaslException("evaluateResponse()", x);
            }
            if (pwd == null) {
                throw new SaslException("No password given");
            }
            try {
                password = new String(this.lookupPassword(id)).getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException x) {
                throw new SaslException("evaluateResponse()", x);
            }
            if (!Arrays.equals(pwd, password)) {
                throw new SaslException("Password incorrect");
            }
            this.complete = true;
            return null;
        }
        catch (NoSuchElementException x) {
            throw new SaslException("evaluateResponse()", x);
        }
    }

    protected String getNegotiatedQOP() {
        return "auth";
    }

    private final char[] lookupPassword(String userName) throws SaslException {
        try {
            if (!this.authenticator.contains(userName)) {
                throw new NoSuchUserException(userName);
            }
            HashMap<String, String> userID = new HashMap<String, String>();
            userID.put("gnu.crypto.sasl.username", userName);
            Map credentials = this.authenticator.lookup(userID);
            String password = (String)credentials.get("gnu.crypto.sasl.password");
            if (password == null) {
                throw new SaslException("lookupPassword()", new InternalError());
            }
            return password.toCharArray();
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new SaslException("lookupPassword()", x);
        }
    }

    public PlainServer() {
        super("PLAIN");
    }
}

