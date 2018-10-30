/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import gnu.crypto.sasl.AuthInfo;
import gnu.crypto.sasl.IAuthInfoProvider;
import gnu.crypto.sasl.IllegalMechanismStateException;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public abstract class ServerMechanism
implements SaslServer {
    protected String mechanism;
    protected String protocol;
    protected String serverName;
    protected Map properties;
    protected CallbackHandler handler;
    protected boolean complete;
    protected String authorizationID;
    protected byte[] channelBinding;
    protected int state;
    protected IAuthInfoProvider authenticator;

    protected abstract void initMechanism() throws SaslException;

    protected abstract void resetMechanism() throws SaslException;

    public abstract byte[] evaluateResponse(byte[] var1) throws SaslException;

    public boolean isComplete() {
        return this.complete;
    }

    public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
        if (!this.isComplete()) {
            throw new IllegalMechanismStateException();
        }
        return this.engineUnwrap(incoming, offset, len);
    }

    public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
        if (!this.isComplete()) {
            throw new IllegalMechanismStateException();
        }
        return this.engineWrap(outgoing, offset, len);
    }

    public String getMechanismName() {
        return this.mechanism;
    }

    public String getAuthorizationID() {
        return this.authorizationID;
    }

    public Object getNegotiatedProperty(String propName) throws SaslException {
        if (!this.isComplete()) {
            throw new IllegalMechanismStateException();
        }
        if ("javax.security.sasl.qop".equals(propName)) {
            return this.getNegotiatedQOP();
        }
        if ("javax.security.sasl.strength".equals(propName)) {
            return this.getNegotiatedStrength();
        }
        if ("javax.security.sasl.server.authentication".equals(propName)) {
            return this.getNegotiatedServerAuth();
        }
        if ("javax.security.sasl.maxbuffer".equals(propName)) {
            return this.getNegotiatedMaxBuffer();
        }
        if ("javax.security.sasl.rawsendsize".equals(propName)) {
            return this.getNegotiatedRawSendSize();
        }
        if ("javax.security.sasl.policy.noplaintext".equals(propName)) {
            return this.getNegotiatedPolicyNoPlainText();
        }
        if ("javax.security.sasl.policy.noactive".equals(propName)) {
            return this.getNegotiatedPolicyNoActive();
        }
        if ("javax.security.sasl.policy.nodictionary".equals(propName)) {
            return this.getNegotiatedPolicyNoDictionary();
        }
        if ("javax.security.sasl.policy.noanonymous".equals(propName)) {
            return this.getNegotiatedPolicyNoAnonymous();
        }
        if ("javax.security.sasl.policy.forward".equals(propName)) {
            return this.getNegotiatedPolicyForwardSecrecy();
        }
        if ("javax.security.sasl.policy.credentials".equals(propName)) {
            return this.getNegotiatedPolicyPassCredentials();
        }
        if ("javax.security.sasl.reuse".equals(propName)) {
            return this.getReuse();
        }
        return null;
    }

    public void dispose() throws SaslException {
        this.reset();
    }

    protected String getNegotiatedQOP() {
        return "auth";
    }

    protected String getNegotiatedStrength() {
        return "low";
    }

    protected String getNegotiatedServerAuth() {
        return "false";
    }

    protected String getNegotiatedMaxBuffer() {
        return null;
    }

    protected String getNegotiatedPolicyNoPlainText() {
        return null;
    }

    protected String getNegotiatedPolicyNoActive() {
        return null;
    }

    protected String getNegotiatedPolicyNoDictionary() {
        return null;
    }

    protected String getNegotiatedPolicyNoAnonymous() {
        return null;
    }

    protected String getNegotiatedPolicyForwardSecrecy() {
        return null;
    }

    protected String getNegotiatedPolicyPassCredentials() {
        return null;
    }

    protected String getNegotiatedRawSendSize() {
        return String.valueOf(2147483643);
    }

    protected String getReuse() {
        return "false";
    }

    protected byte[] engineUnwrap(byte[] incoming, int offset, int len) throws SaslException {
        byte[] result = new byte[len];
        System.arraycopy(incoming, offset, result, 0, len);
        return result;
    }

    protected byte[] engineWrap(byte[] outgoing, int offset, int len) throws SaslException {
        byte[] result = new byte[len];
        System.arraycopy(outgoing, offset, result, 0, len);
        return result;
    }

    public void init(Map attributes) throws SaslException {
        if (this.state != -1) {
            throw new IllegalMechanismStateException("init()");
        }
        if (this.properties == null) {
            this.properties = new HashMap<K, V>();
        } else {
            this.properties.clear();
        }
        if (attributes != null) {
            this.protocol = (String)attributes.get("gnu.crypto.sasl.protocol");
            this.serverName = (String)attributes.get("gnu.crypto.sasl.server.name");
            this.handler = (CallbackHandler)attributes.get("gnu.crypto.sasl.callback.handler");
            this.channelBinding = (byte[])attributes.get("gnu.crypto.sasl.channel.binding");
            this.properties.putAll(attributes);
        } else {
            this.handler = null;
        }
        if (this.protocol == null) {
            this.protocol = "";
        }
        if (this.serverName == null) {
            this.serverName = "";
        }
        if (this.authenticator != null) {
            this.authenticator.activate(this.properties);
        }
        if (this.channelBinding == null) {
            this.channelBinding = new byte[0];
        }
        this.initMechanism();
        this.complete = false;
        this.state = 0;
    }

    public void reset() throws SaslException {
        this.resetMechanism();
        this.properties.clear();
        if (this.authenticator != null) {
            this.authenticator.passivate();
        }
        this.serverName = null;
        this.protocol = null;
        this.channelBinding = null;
        this.complete = false;
        this.state = -1;
    }

    private final /* synthetic */ void this() {
        this.complete = false;
        this.state = -1;
    }

    protected ServerMechanism(String mechanism) {
        this.this();
        this.mechanism = mechanism;
        this.authenticator = AuthInfo.getProvider(mechanism);
        this.state = -1;
    }
}

