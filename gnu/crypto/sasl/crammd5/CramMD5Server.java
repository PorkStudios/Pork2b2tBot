/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.crammd5;

import gnu.crypto.sasl.IAuthInfoProvider;
import gnu.crypto.sasl.NoSuchUserException;
import gnu.crypto.sasl.ServerMechanism;
import gnu.crypto.sasl.crammd5.CramMD5Util;
import gnu.crypto.util.Util;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.security.sasl.AuthenticationException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

public class CramMD5Server
extends ServerMechanism
implements SaslServer {
    private byte[] msgID;

    protected void initMechanism() throws SaslException {
    }

    protected void resetMechanism() throws SaslException {
    }

    public byte[] evaluateResponse(byte[] response) throws SaslException {
        byte[] digest;
        byte[] responseDigest;
        if (this.state == 0) {
            this.msgID = CramMD5Util.createMsgID();
            ++this.state;
            return this.msgID;
        }
        String responseStr = new String(response);
        int index = responseStr.lastIndexOf(" ");
        String username = responseStr.substring(0, index);
        try {
            responseDigest = responseStr.substring(index + 1).getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException x) {
            throw new AuthenticationException("evaluateResponse()", x);
        }
        char[] password = this.lookupPassword(username);
        try {
            digest = CramMD5Util.createHMac(password, this.msgID);
        }
        catch (InvalidKeyException x) {
            throw new AuthenticationException("evaluateResponse()", x);
        }
        try {
            digest = Util.toString(digest).toLowerCase().getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException x) {
            throw new AuthenticationException("evaluateResponse()", x);
        }
        if (!Arrays.equals(digest, responseDigest)) {
            throw new AuthenticationException("Digest mismatch");
        }
        ++this.state;
        return null;
    }

    public boolean isComplete() {
        boolean bl = false;
        if (this.state == 2) {
            bl = true;
        }
        return bl;
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
                throw new AuthenticationException("lookupPassword()", new InternalError());
            }
            return password.toCharArray();
        }
        catch (IOException x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new AuthenticationException("lookupPassword()", x);
        }
    }

    public CramMD5Server() {
        super("CRAM-MD5");
    }
}

