/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import gnu.crypto.sasl.ClientMechanism;
import gnu.crypto.sasl.anonymous.AnonymousClient;
import gnu.crypto.sasl.crammd5.CramMD5Client;
import gnu.crypto.sasl.plain.PlainClient;
import gnu.crypto.sasl.srp.SRPClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;

public class ClientFactory
implements SaslClientFactory {
    public static final Set getNames() {
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(ClientFactory.getNamesInternal(null))));
    }

    private static final String[] getNamesInternal(Map props) {
        String[] all = new String[]{"SRP", "CRAM-MD5", "PLAIN", "ANONYMOUS"};
        if (props == null) {
            return all;
        }
        if (ClientFactory.hasPolicy("javax.security.sasl.policy.credentials", props)) {
            return new String[0];
        }
        ArrayList<String> result = new ArrayList<String>(all.length);
        int i = 0;
        while (i < all.length) {
            result.add(all[i++]);
        }
        if (ClientFactory.hasPolicy("javax.security.sasl.policy.noplaintext", props)) {
            result.remove("PLAIN");
        }
        if (ClientFactory.hasPolicy("javax.security.sasl.policy.noactive", props)) {
            result.remove("CRAM-MD5");
            result.remove("PLAIN");
        }
        if (ClientFactory.hasPolicy("javax.security.sasl.policy.nodictionary", props)) {
            result.remove("CRAM-MD5");
            result.remove("PLAIN");
        }
        if (ClientFactory.hasPolicy("javax.security.sasl.policy.noanonymous", props)) {
            result.remove("ANONYMOUS");
        }
        if (ClientFactory.hasPolicy("javax.security.sasl.policy.forward", props)) {
            result.remove("CRAM-MD5");
            result.remove("ANONYMOUS");
            result.remove("PLAIN");
        }
        return result.toArray(new String[0]);
    }

    public static final ClientMechanism getInstance(String mechanism) {
        if (mechanism == null) {
            return null;
        }
        if ((mechanism = mechanism.trim().toUpperCase()).equals("SRP")) {
            return new SRPClient();
        }
        if (mechanism.equals("CRAM-MD5")) {
            return new CramMD5Client();
        }
        if (mechanism.equals("PLAIN")) {
            return new PlainClient();
        }
        if (mechanism.equals("ANONYMOUS")) {
            return new AnonymousClient();
        }
        return null;
    }

    public SaslClient createSaslClient(String[] mechanisms, String authorisationID, String protocol, String serverName, Map props, CallbackHandler cbh) throws SaslException {
        ClientMechanism result = null;
        int i = 0;
        while (i < mechanisms.length) {
            String mechanism = mechanisms[i];
            result = ClientFactory.getInstance(mechanism);
            if (result != null) break;
            ++i;
        }
        if (result != null) {
            HashMap<String, Object> attributes = new HashMap<String, Object>();
            if (props != null) {
                attributes.putAll(props);
            }
            attributes.put("gnu.crypto.sasl.authorisation.ID", authorisationID);
            attributes.put("gnu.crypto.sasl.protocol", protocol);
            attributes.put("gnu.crypto.sasl.server.name", serverName);
            attributes.put("gnu.crypto.sasl.callback.handler", cbh);
            result.init(attributes);
            return result;
        }
        throw new SaslException("No supported mechanism found in given mechanism list");
    }

    public String[] getMechanismNames(Map props) {
        return ClientFactory.getNamesInternal(props);
    }

    private static final boolean hasPolicy(String propertyName, Map props) {
        return "true".equalsIgnoreCase(String.valueOf(props.get(propertyName)));
    }
}

