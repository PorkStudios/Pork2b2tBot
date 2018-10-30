/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.plain;

import gnu.crypto.sasl.ClientMechanism;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class PlainClient
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
        try {
            String username;
            NameCallback nameCB;
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
            StringBuffer sb = new StringBuffer();
            if (this.authorizationID != null) {
                sb.append(this.authorizationID);
            }
            sb.append('\u0000');
            sb.append(username);
            sb.append('\u0000');
            sb.append(password);
            this.complete = true;
            byte[] response = sb.toString().getBytes("UTF-8");
            return response;
        }
        catch (Exception x) {
            if (x instanceof SaslException) {
                throw (SaslException)x;
            }
            throw new SaslException("evaluateChallenge()", x);
        }
    }

    protected String getNegotiatedQOP() {
        return "auth";
    }

    public PlainClient() {
        super("PLAIN");
    }
}

