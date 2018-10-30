/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import gnu.crypto.key.IKeyPairCodec;
import gnu.crypto.key.KeyPairGeneratorFactory;
import gnu.crypto.key.dh.DHKeyPairRawCodec;
import gnu.crypto.key.dh.GnuDHPrivateKey;
import gnu.crypto.key.dh.GnuDHPublicKey;
import gnu.crypto.key.dss.DSSKeyPairRawCodec;
import gnu.crypto.key.dss.DSSPrivateKey;
import gnu.crypto.key.dss.DSSPublicKey;
import gnu.crypto.key.rsa.GnuRSAPrivateKey;
import gnu.crypto.key.rsa.GnuRSAPublicKey;
import gnu.crypto.key.rsa.RSAKeyPairRawCodec;
import gnu.crypto.key.srp6.SRPKeyPairRawCodec;
import gnu.crypto.key.srp6.SRPPrivateKey;
import gnu.crypto.key.srp6.SRPPublicKey;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Set;

public class KeyPairCodecFactory {
    public static IKeyPairCodec getInstance(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        IKeyPairCodec result = null;
        if (name.equalsIgnoreCase("dsa") || name.equals("dss")) {
            result = new DSSKeyPairRawCodec();
        } else if (name.equalsIgnoreCase("rsa")) {
            result = new RSAKeyPairRawCodec();
        } else if (name.equalsIgnoreCase("dh")) {
            result = new DHKeyPairRawCodec();
        } else if (name.equalsIgnoreCase("srp")) {
            result = new SRPKeyPairRawCodec();
        }
        return result;
    }

    public static IKeyPairCodec getInstance(byte[] buffer) {
        if (buffer == null) {
            return null;
        }
        if (buffer.length < 5) {
            return null;
        }
        if (buffer[0] != 71) {
            return null;
        }
        if (buffer[1] != 1) {
            return null;
        }
        IKeyPairCodec result = null;
        switch (buffer[2]) {
            case 68: {
                result = new DSSKeyPairRawCodec();
                break;
            }
            case 82: {
                result = new RSAKeyPairRawCodec();
                break;
            }
            case 72: {
                result = new DHKeyPairRawCodec();
                break;
            }
            case 83: {
                result = new SRPKeyPairRawCodec();
                break;
            }
        }
        return result;
    }

    public static IKeyPairCodec getInstance(Key key) {
        if (key == null) {
            return null;
        }
        IKeyPairCodec result = null;
        if (key instanceof PublicKey) {
            if (key instanceof DSSPublicKey) {
                result = new DSSKeyPairRawCodec();
            } else if (key instanceof GnuRSAPublicKey) {
                result = new RSAKeyPairRawCodec();
            } else if (key instanceof GnuDHPublicKey) {
                result = new DHKeyPairRawCodec();
            } else if (key instanceof SRPPublicKey) {
                result = new SRPKeyPairRawCodec();
            }
        } else if (key instanceof PrivateKey) {
            if (key instanceof DSSPrivateKey) {
                result = new DSSKeyPairRawCodec();
            } else if (key instanceof GnuRSAPrivateKey) {
                result = new RSAKeyPairRawCodec();
            } else if (key instanceof GnuDHPrivateKey) {
                result = new DHKeyPairRawCodec();
            } else if (key instanceof SRPPrivateKey) {
                result = new SRPKeyPairRawCodec();
            }
        }
        return result;
    }

    public static final Set getNames() {
        return KeyPairGeneratorFactory.getNames();
    }

    private KeyPairCodecFactory() {
    }
}

