/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.crammd5;

import gnu.crypto.sasl.ClientMechanism;
import gnu.crypto.sasl.crammd5.CramMD5Util;
import gnu.crypto.util.Util;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthenticationException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class CramMD5Client
extends ClientMechanism
implements SaslClient {
    protected void initMechanism() throws SaslException {
    }

    protected void resetMechanism() throws SaslException {
    }

    public boolean hasInitialResponse() {
        return false;
    }

    public byte[] evaluateChallenge(byte[] challenge) throws SaslException {
        if (challenge == null) {
            throw new SaslException("null challenge");
        }
        try {
            String username;
            NameCallback nameCB;
            byte[] digest;
            char[] password;
            String defaultName;
            if (!this.properties.containsKey("gnu.crypto.sasl.username") && !this.properties.containsKey("gnu.crypto.sasl.password")) {
                Callback[] callbacks = new Callback[2];
                defaultName = System.getProperty("user.name");
                nameCB = defaultName == null ? new NameCallback("username: ") : new NameCallback("username: ", defaultName);
                PasswordCallback pwdCB = new PasswordCallback("password: ", false);
                callbacks[0] = nameCB;
                callbacks[1] = pwdCB;
                this.handler.handle(callbacks);
                username = nameCB.getName();
                password = pwdCB.getPassword();
            } else {
                Callback[] callbacks;
                if (this.properties.containsKey("gnu.crypto.sasl.username")) {
                    username = (String)this.properties.get("gnu.crypto.sasl.username");
                } else {
                    callbacks = new Callback[1];
                    defaultName = System.getProperty("user.name");
                    nameCB = defaultName == null ? new NameCallback("username: ") : new NameCallback("username: ", defaultName);
                    callbacks[0] = nameCB;
                    this.handler.handle(callbacks);
                    username = nameCB.getName();
                }
                if (this.properties.containsKey("gnu.crypto.sasl.password")) {
                    password = ((String)this.properties.get("gnu.crypto.sasl.password")).toCharArray();
                } else {
                    callbacks = new Callback[1];
                    PasswordCallback pwdCB = new PasswordCallback("password: ", false);
                    callbacks[0] = pwdCB;
                    this.handler.handle(callbacks);
                    password = pwdCB.getPassword();
                }
            }
            if (password == null) {
                throw new SaslException("null password supplied");
            }
            try {
                digest = CramMD5Util.createHMac(password, challenge);
            }
            catch (InvalidKeyException x) {
                throw new AuthenticationException("evaluateChallenge()", x);
            }
            String response = username + ' ' + Util.toString(digest).toLowerCase();
            this.complete = true;
            return response.getBytes("UTF-8");
        }
        catch (UnsupportedCallbackException x) {
            throw new AuthenticationException("evaluateChallenge()", x);
        }
        catch (IOException x) {
            throw new AuthenticationException("evaluateChallenge()", x);
        }
    }

    protected String getNegotiatedQOP() {
        return "auth";
    }

    public CramMD5Client() {
        super("CRAM-MD5");
    }
}

