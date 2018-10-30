/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.key.GnuSecretKey;
import gnu.crypto.key.IKeyPairCodec;
import gnu.crypto.key.KeyPairCodecFactory;
import gnu.crypto.key.dh.GnuDHPrivateKey;
import gnu.crypto.key.dss.DSSPrivateKey;
import gnu.crypto.key.rsa.GnuRSAPrivateKey;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.PrimitiveEntry;
import gnu.crypto.keyring.Properties;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

public final class PrivateKeyEntry
extends PrimitiveEntry {
    public static final int TYPE = 7;
    private Key key;

    public static final PrivateKeyEntry decode(DataInputStream in) throws IOException {
        PrivateKeyEntry entry = new PrivateKeyEntry();
        entry.defaultDecode(in);
        String type = entry.properties.get("type");
        if (type == null) {
            throw new MalformedKeyringException("no key type");
        }
        if (type.equalsIgnoreCase("RAW-DSS")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dss");
            entry.key = coder.decodePrivateKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW-RSA")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("rsa");
            entry.key = coder.decodePrivateKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW-DH")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dh");
            entry.key = coder.decodePrivateKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW")) {
            entry.key = new GnuSecretKey(entry.payload, null);
        } else if (type.equalsIgnoreCase("PKCS8")) {
            KeyFactory kf;
            try {
                kf = KeyFactory.getInstance("RSA");
                entry.key = kf.generatePrivate(new PKCS8EncodedKeySpec(entry.payload));
            }
            catch (Exception exception) {}
            if (entry.key == null) {
                try {
                    kf = KeyFactory.getInstance("DSA");
                    entry.key = kf.generatePrivate(new PKCS8EncodedKeySpec(entry.payload));
                }
                catch (Exception exception) {}
                if (entry.key == null) {
                    throw new MalformedKeyringException("could not decode PKCS#8 key");
                }
            }
        } else {
            throw new MalformedKeyringException("unsupported key type " + type);
        }
        return entry;
    }

    public final Key getKey() {
        return this.key;
    }

    protected final void encodePayload() throws IOException {
        String format = this.key.getFormat();
        if (this.key instanceof DSSPrivateKey) {
            this.properties.put("type", "RAW-DSS");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dss");
            this.payload = coder.encodePrivateKey((PrivateKey)this.key);
        } else if (this.key instanceof GnuRSAPrivateKey) {
            this.properties.put("type", "RAW-RSA");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("rsa");
            this.payload = coder.encodePrivateKey((PrivateKey)this.key);
        } else if (this.key instanceof GnuDHPrivateKey) {
            this.properties.put("type", "RAW-DH");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dh");
            this.payload = coder.encodePrivateKey((PrivateKey)this.key);
        } else if (this.key instanceof GnuSecretKey) {
            this.properties.put("type", "RAW");
            this.payload = this.key.getEncoded();
        } else if (format != null && format.equals("PKCS#8")) {
            this.properties.put("type", "PKCS8");
            this.payload = this.key.getEncoded();
        } else {
            throw new IllegalArgumentException("unsupported private key");
        }
    }

    public PrivateKeyEntry(Key key, Date creationDate, Properties properties) {
        super(7, creationDate, properties);
        if (key == null) {
            throw new IllegalArgumentException("no private key");
        }
        if (!(key instanceof PrivateKey) && !(key instanceof GnuSecretKey)) {
            throw new IllegalArgumentException("not a private or secret key");
        }
        this.key = key;
    }

    private PrivateKeyEntry() {
    }
}

