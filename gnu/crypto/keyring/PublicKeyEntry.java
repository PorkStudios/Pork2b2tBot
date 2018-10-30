/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.key.IKeyPairCodec;
import gnu.crypto.key.KeyPairCodecFactory;
import gnu.crypto.key.dh.GnuDHPublicKey;
import gnu.crypto.key.dss.DSSPublicKey;
import gnu.crypto.key.rsa.GnuRSAPublicKey;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.PrimitiveEntry;
import gnu.crypto.keyring.Properties;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

public final class PublicKeyEntry
extends PrimitiveEntry {
    public static final int TYPE = 6;
    private PublicKey key;

    public static final PublicKeyEntry decode(DataInputStream in) throws IOException {
        PublicKeyEntry entry = new PublicKeyEntry();
        entry.defaultDecode(in);
        String type = entry.properties.get("type");
        if (type == null) {
            throw new MalformedKeyringException("no key type");
        }
        if (type.equalsIgnoreCase("RAW-DSS")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dss");
            entry.key = coder.decodePublicKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW-RSA")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("rsa");
            entry.key = coder.decodePublicKey(entry.payload);
        } else if (type.equalsIgnoreCase("RAW-DH")) {
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dh");
            entry.key = coder.decodePublicKey(entry.payload);
        } else if (type.equalsIgnoreCase("X.509")) {
            KeyFactory kf;
            try {
                kf = KeyFactory.getInstance("RSA");
                entry.key = kf.generatePublic(new X509EncodedKeySpec(entry.payload));
            }
            catch (Exception exception) {}
            if (entry.key == null) {
                try {
                    kf = KeyFactory.getInstance("DSA");
                    entry.key = kf.generatePublic(new X509EncodedKeySpec(entry.payload));
                }
                catch (Exception exception) {}
                if (entry.key == null) {
                    throw new MalformedKeyringException("could not decode X.509 key");
                }
            }
        } else {
            throw new MalformedKeyringException("unsupported public key type: " + type);
        }
        return entry;
    }

    public final PublicKey getKey() {
        return this.key;
    }

    protected final void encodePayload() throws IOException {
        if (this.key instanceof DSSPublicKey) {
            this.properties.put("type", "RAW-DSS");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dss");
            this.payload = coder.encodePublicKey(this.key);
        } else if (this.key instanceof GnuRSAPublicKey) {
            this.properties.put("type", "RAW-RSA");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("rsa");
            this.payload = coder.encodePublicKey(this.key);
        } else if (this.key instanceof GnuDHPublicKey) {
            this.properties.put("type", "RAW-DH");
            IKeyPairCodec coder = KeyPairCodecFactory.getInstance("dh");
            this.payload = coder.encodePublicKey(this.key);
        } else if (this.key.getFormat() != null && this.key.getFormat().equals("X.509")) {
            this.properties.put("type", "X.509");
            this.payload = this.key.getEncoded();
        } else {
            throw new IllegalArgumentException("cannot encode public key");
        }
    }

    public PublicKeyEntry(PublicKey key, Date creationDate, Properties properties) {
        super(6, creationDate, properties);
        if (key == null) {
            throw new IllegalArgumentException("no key specified");
        }
        this.key = key;
    }

    private PublicKeyEntry() {
    }
}

