/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.Registry;
import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.MaskableEnvelopeEntry;
import gnu.crypto.keyring.Properties;
import gnu.crypto.mac.IMac;
import gnu.crypto.mac.MacFactory;
import gnu.crypto.mac.MacOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class AuthenticatedEntry
extends MaskableEnvelopeEntry
implements Registry {
    public static final int TYPE = 2;

    public static final AuthenticatedEntry decode(DataInputStream in) throws IOException {
        AuthenticatedEntry entry = new AuthenticatedEntry();
        entry.properties = new Properties();
        entry.properties.decode(in);
        if (!entry.properties.containsKey("mac")) {
            throw new MalformedKeyringException("no mac specified");
        }
        if (!entry.properties.containsKey("maclen")) {
            throw new MalformedKeyringException("no mac length specified");
        }
        return entry;
    }

    public final void authenticate(byte[] key) throws IOException, InvalidKeyException {
        if (this.isMasked()) {
            throw new IllegalStateException("entry is masked");
        }
        IMac m = this.getMac(key);
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
        MacOutputStream macout = new MacOutputStream(bout, m);
        DataOutputStream out2 = new DataOutputStream(macout);
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry)it.next();
            entry.encode(out2);
        }
        bout.write(m.digest());
        this.payload = bout.toByteArray();
    }

    public final void verify(byte[] key) throws InvalidKeyException {
        if (!this.isMasked() || this.payload == null) {
            return;
        }
        IMac m = this.getMac(key);
        m.update(this.payload, 0, this.payload.length - m.macSize());
        byte[] macValue = new byte[m.macSize()];
        System.arraycopy(this.payload, this.payload.length - macValue.length, macValue, 0, macValue.length);
        if (!Arrays.equals(macValue, m.digest())) {
            throw new IllegalArgumentException("MAC verification failed");
        }
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(this.payload, 0, this.payload.length - m.macSize()));
            this.decodeEnvelope(in);
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException("malformed keyring fragment");
        }
        this.setMasked(false);
        this.payload = null;
    }

    protected final void encodePayload() throws IOException {
        if (this.payload == null) {
            throw new IllegalStateException("not authenticated");
        }
    }

    private final IMac getMac(byte[] key) throws InvalidKeyException {
        IMac mac = MacFactory.getInstance(this.properties.get("mac"));
        if (mac == null) {
            throw new IllegalArgumentException("no such mac: " + this.properties.get("mac"));
        }
        int maclen = 0;
        if (!this.properties.containsKey("maclen")) {
            throw new IllegalArgumentException("no MAC length");
        }
        try {
            maclen = Integer.parseInt(this.properties.get("maclen"));
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("bad MAC length");
        }
        HashMap<String, Object> macAttr = new HashMap<String, Object>();
        macAttr.put("gnu.crypto.mac.key.material", key);
        macAttr.put("gnu.crypto.mac.truncated.size", new Integer(maclen));
        mac.init(macAttr);
        return mac;
    }

    public AuthenticatedEntry(String mac, int macLen, Properties properties) {
        super(2, properties);
        if (macLen <= 0) {
            throw new IllegalArgumentException("invalid mac length");
        }
        this.properties.put("mac", mac);
        this.properties.put("maclen", String.valueOf(macLen));
        this.setMasked(false);
    }

    private AuthenticatedEntry() {
        this.setMasked(true);
    }
}

