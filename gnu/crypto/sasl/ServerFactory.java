/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import gnu.crypto.sasl.ServerMechanism;
import gnu.crypto.sasl.anonymous.AnonymousServer;
import gnu.crypto.sasl.crammd5.CramMD5Server;
import gnu.crypto.sasl.plain.PlainServer;
import gnu.crypto.sasl.srp.SRPServer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

public class ServerFactory
implements SaslServerFactory {
    public static final Set getNames() {
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(ServerFactory.getNamesInternal(null))));
    }

    private static final String[] getNamesInternal(Map props) {
        String[] all = new String[]{"SRP", "CRAM-MD5", "PLAIN", "ANONYMOUS"};
        ArrayList<String> result = new ArrayList<String>(4);
        int i = 0;
        while (i < all.length) {
            result.add(all[i++]);
        }
        if (props == null) {
            return result.toArray(new String[0]);
        }
        if (ServerFactory.hasPolicy("javax.security.sasl.policy.credentials", props)) {
            return new String[0];
        }
        if (ServerFactory.hasPolicy("javax.security.sasl.policy.noplaintext", props)) {
            result.remove("PLAIN");
        }
        if (ServerFactory.hasPolicy("javax.security.sasl.policy.noactive", props)) {
            result.remove("CRAM-MD5");
            result.remove("PLAIN");
        }
        if (ServerFactory.hasPolicy("javax.security.sasl.policy.nodictionary", props)) {
            result.remove("CRAM-MD5");
            result.remove("PLAIN");
        }
        if (ServerFactory.hasPolicy("javax.security.sasl.policy.noanonymous", props)) {
            result.remove("ANONYMOUS");
        }
        if (ServerFactory.hasPolicy("javax.security.sasl.policy.forward", props)) {
            result.remove("CRAM-MD5");
            result.remove("ANONYMOUS");
            result.remove("PLAIN");
        }
        return result.toArray(new String[0]);
    }

    public static final ServerMechanism getInstance(String mechanism) {
        if (mechanism == null) {
            return null;
        }
        if ((mechanism = mechanism.trim().toUpperCase()).equals("SRP")) {
            return new SRPServer();
        }
        if (mechanism.equals("CRAM-MD5")) {
            return new CramMD5Server();
        }
        if (mechanism.equals("PLAIN")) {
            return new PlainServer();
        }
        if (mechanism.equals("ANONYMOUS")) {
            return new AnonymousServer();
        }
        return null;
    }

    public SaslServer createSaslServer(String mechanism, String protocol, String serverName, Map props, CallbackHandler cbh) throws SaslException {
        ServerMechanism result = ServerFactory.getInstance(mechanism);
        if (result != null) {
            HashMap<String, Object> attributes = new HashMap<String, Object>();
            if (props != null) {
                attributes.putAll(props);
            }
            attributes.put("gnu.crypto.sasl.protocol", protocol);
            attributes.put("gnu.crypto.sasl.server.name", serverName);
            attributes.put("gnu.crypto.sasl.callback.handler", cbh);
            result.init(attributes);
        }
        return result;
    }

    public String[] getMechanismNames(Map props) {
        return ServerFactory.getNamesInternal(props);
    }

    private static final boolean hasPolicy(String propertyName, Map props) {
        return "true".equalsIgnoreCase(String.valueOf(props.get(propertyName)));
    }
}

