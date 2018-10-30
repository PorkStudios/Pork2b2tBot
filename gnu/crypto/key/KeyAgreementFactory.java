/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import gnu.crypto.key.BaseKeyAgreementParty;
import gnu.crypto.key.IKeyAgreementParty;
import gnu.crypto.key.dh.DiffieHellmanReceiver;
import gnu.crypto.key.dh.DiffieHellmanSender;
import gnu.crypto.key.dh.ElGamalReceiver;
import gnu.crypto.key.dh.ElGamalSender;
import gnu.crypto.key.srp6.SRP6Host;
import gnu.crypto.key.srp6.SRP6SaslClient;
import gnu.crypto.key.srp6.SRP6SaslServer;
import gnu.crypto.key.srp6.SRP6TLSClient;
import gnu.crypto.key.srp6.SRP6TLSServer;
import gnu.crypto.key.srp6.SRP6User;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class KeyAgreementFactory {
    public static IKeyAgreementParty getPartyAInstance(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        BaseKeyAgreementParty result = null;
        if (name.equalsIgnoreCase("dh")) {
            result = new DiffieHellmanSender();
        } else if (name.equalsIgnoreCase("elgamal")) {
            result = new ElGamalSender();
        } else if (name.equalsIgnoreCase("srp6")) {
            result = new SRP6User();
        } else if (name.equalsIgnoreCase("srp-sasl")) {
            result = new SRP6SaslClient();
        } else if (name.equalsIgnoreCase("srp-tls")) {
            result = new SRP6TLSClient();
        }
        return result;
    }

    public static IKeyAgreementParty getPartyBInstance(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        BaseKeyAgreementParty result = null;
        if (name.equalsIgnoreCase("dh")) {
            result = new DiffieHellmanReceiver();
        } else if (name.equalsIgnoreCase("elgamal")) {
            result = new ElGamalReceiver();
        } else if (name.equalsIgnoreCase("srp6")) {
            result = new SRP6Host();
        } else if (name.equalsIgnoreCase("srp-sasl")) {
            result = new SRP6SaslServer();
        } else if (name.equalsIgnoreCase("srp-tls")) {
            result = new SRP6TLSServer();
        }
        return result;
    }

    public static final Set getNames() {
        HashSet<String> hs = new HashSet<String>();
        hs.add("dh");
        hs.add("elgamal");
        hs.add("srp6");
        hs.add("srp-sasl");
        hs.add("srp-tls");
        return Collections.unmodifiableSet(hs);
    }

    private KeyAgreementFactory() {
    }
}

