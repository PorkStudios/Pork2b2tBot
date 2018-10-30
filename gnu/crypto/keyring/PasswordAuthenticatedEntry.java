/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.Registry;
import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.MaskableEnvelopeEntry;
import gnu.crypto.keyring.MeteredInputStream;
import gnu.crypto.keyring.PasswordProtectedEntry;
import gnu.crypto.keyring.Properties;
import gnu.crypto.mac.IMac;
import gnu.crypto.mac.MacFactory;
import gnu.crypto.mac.MacInputStream;
import gnu.crypto.mac.MacOutputStream;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.PRNGFactory;
import gnu.crypto.util.PRNG;
import gnu.crypto.util.Util;
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

public final class PasswordAuthenticatedEntry
extends MaskableEnvelopeEntry
implements PasswordProtectedEntry,
Registry {
    public static final int TYPE = 3;

    public static final PasswordAuthenticatedEntry decode(DataInputStream in, char[] password) throws IOException {
        PasswordAuthenticatedEntry entry = new PasswordAuthenticatedEntry();
        entry.properties = new Properties();
        entry.properties.decode(in);
        IMac mac = entry.getMac(password);
        int len = in.readInt() - mac.macSize();
        MeteredInputStream min = new MeteredInputStream(in, len);
        MacInputStream macin = new MacInputStream(min, mac);
        DataInputStream in2 = new DataInputStream(macin);
        entry.setMasked(false);
        entry.decodeEnvelope(in2);
        byte[] macValue = new byte[mac.macSize()];
        in.readFully(macValue);
        if (!Arrays.equals(macValue, mac.digest())) {
            throw new MalformedKeyringException("MAC verification failed");
        }
        return entry;
    }

    public static final PasswordAuthenticatedEntry decode(DataInputStream in) throws IOException {
        PasswordAuthenticatedEntry entry = new PasswordAuthenticatedEntry();
        entry.defaultDecode(in);
        if (!entry.properties.containsKey("mac")) {
            throw new MalformedKeyringException("no MAC");
        }
        if (!entry.properties.containsKey("maclen")) {
            throw new MalformedKeyringException("no MAC length");
        }
        if (!entry.properties.containsKey("salt")) {
            throw new MalformedKeyringException("no salt");
        }
        return entry;
    }

    public final void verify(char[] password) {
        if (!this.isMasked() || this.payload == null) {
            return;
        }
        IMac m = null;
        try {
            m = this.getMac(password);
        }
        catch (Exception x) {
            throw new IllegalArgumentException(x.toString());
        }
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

    public final void authenticate(char[] password) throws IOException {
        if (this.isMasked()) {
            throw new IllegalStateException("entry is masked");
        }
        byte[] salt = new byte[8];
        PRNG.nextBytes(salt, 0, salt.length);
        this.properties.put("salt", Util.toString(salt));
        IMac m = this.getMac(password);
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

    public final void encode(DataOutputStream out, char[] password) throws IOException {
        this.authenticate(password);
        this.encode(out);
    }

    protected final void encodePayload(DataOutputStream out) throws IOException {
        if (this.payload == null) {
            throw new IllegalStateException("mac not computed");
        }
    }

    private final IMac getMac(char[] password) throws MalformedKeyringException {
        if (!this.properties.containsKey("salt")) {
            throw new MalformedKeyringException("no salt");
        }
        byte[] salt = Util.toBytesFromString(this.properties.get("salt"));
        IMac mac = MacFactory.getInstance(this.properties.get("mac"));
        if (mac == null) {
            throw new MalformedKeyringException("no such mac: " + this.properties.get("mac"));
        }
        int keylen = mac.macSize();
        int maclen = 0;
        if (!this.properties.containsKey("maclen")) {
            throw new MalformedKeyringException("no MAC length");
        }
        try {
            maclen = Integer.parseInt(this.properties.get("maclen"));
        }
        catch (NumberFormatException nfe) {
            throw new MalformedKeyringException("bad MAC length");
        }
        HashMap<String, Object> pbAttr = new HashMap<String, Object>();
        pbAttr.put("gnu.crypto.pbe.password", password);
        pbAttr.put("gnu.crypto.pbe.salt", salt);
        pbAttr.put("gnu.crypto.pbe.iteration.count", ITERATION_COUNT);
        IRandom kdf = PRNGFactory.getInstance("PBKDF2-HMAC-SHA");
        kdf.init(pbAttr);
        byte[] dk = new byte[keylen];
        try {
            kdf.nextBytes(dk, 0, keylen);
        }
        catch (LimitReachedException shouldNotHappen) {
            throw new Error(shouldNotHappen.toString());
        }
        HashMap<String, Object> macAttr = new HashMap<String, Object>();
        macAttr.put("gnu.crypto.mac.key.material", dk);
        macAttr.put("gnu.crypto.mac.truncated.size", new Integer(maclen));
        try {
            mac.init(macAttr);
        }
        catch (InvalidKeyException shouldNotHappen) {
            throw new Error(shouldNotHappen.toString());
        }
        return mac;
    }

    public PasswordAuthenticatedEntry(String mac, int maclen, Properties properties) {
        super(3, properties);
        if (mac == null || mac.length() == 0) {
            throw new IllegalArgumentException("no MAC specified");
        }
        this.properties.put("mac", mac);
        this.properties.put("maclen", String.valueOf(maclen));
        this.setMasked(false);
    }

    private PasswordAuthenticatedEntry() {
        this.setMasked(true);
    }
}

