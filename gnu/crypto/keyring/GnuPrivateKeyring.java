/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.BaseKeyring;
import gnu.crypto.keyring.CertPathEntry;
import gnu.crypto.keyring.CompressedEntry;
import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.IPrivateKeyring;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.PasswordAuthenticatedEntry;
import gnu.crypto.keyring.PasswordEncryptedEntry;
import gnu.crypto.keyring.PrivateKeyEntry;
import gnu.crypto.keyring.Properties;
import gnu.crypto.keyring.PublicKeyEntry;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class GnuPrivateKeyring
extends BaseKeyring
implements IPrivateKeyring {
    public static final int USAGE = 3;
    protected String mac;
    protected int maclen;
    protected String cipher;
    protected String mode;
    protected int keylen;

    public boolean containsPrivateKey(String alias) {
        if (!this.containsAlias(alias)) {
            return false;
        }
        List l = this.get(alias);
        Iterator it = l.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof PasswordAuthenticatedEntry)) continue;
            return true;
        }
        return false;
    }

    public Key getPrivateKey(String alias, char[] password) throws UnrecoverableKeyException {
        Entry e;
        if (!this.containsAlias(alias)) {
            return null;
        }
        List l = this.get(alias);
        PasswordAuthenticatedEntry e1 = null;
        PasswordEncryptedEntry e2 = null;
        Iterator it = l.iterator();
        while (it.hasNext()) {
            e = (Entry)it.next();
            if (!(e instanceof PasswordAuthenticatedEntry)) continue;
            e1 = (PasswordAuthenticatedEntry)e;
            break;
        }
        if (e1 == null) {
            return null;
        }
        try {
            e1.verify(password);
        }
        catch (Exception e3) {
            throw new UnrecoverableKeyException("authentication failed");
        }
        it = e1.getEntries().iterator();
        while (it.hasNext()) {
            e = (Entry)it.next();
            if (!(e instanceof PasswordEncryptedEntry)) continue;
            e2 = (PasswordEncryptedEntry)e;
            break;
        }
        if (e2 == null) {
            return null;
        }
        try {
            e2.decrypt(password);
        }
        catch (Exception e4) {
            throw new UnrecoverableKeyException("decryption failed");
        }
        it = e2.get(alias).iterator();
        while (it.hasNext()) {
            e = (Entry)it.next();
            if (!(e instanceof PrivateKeyEntry)) continue;
            return ((PrivateKeyEntry)e).getKey();
        }
        return null;
    }

    public void putPrivateKey(String alias, Key key, char[] password) {
        if (this.containsPrivateKey(alias)) {
            return;
        }
        alias = this.fixAlias(alias);
        Properties p = new Properties();
        p.put("alias", alias);
        PrivateKeyEntry pke = new PrivateKeyEntry(key, new Date(), p);
        PasswordEncryptedEntry enc = new PasswordEncryptedEntry(this.cipher, this.mode, this.keylen, new Properties());
        PasswordAuthenticatedEntry auth = new PasswordAuthenticatedEntry(this.mac, this.maclen, new Properties());
        enc.add(pke);
        auth.add(enc);
        try {
            enc.encode(null, password);
            auth.encode(null, password);
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException(ioe.toString());
        }
        this.keyring.add(auth);
    }

    public boolean containsPublicKey(String alias) {
        if (!this.containsAlias(alias)) {
            return false;
        }
        List l = this.get(alias);
        Iterator it = l.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof PublicKeyEntry)) continue;
            return true;
        }
        return false;
    }

    public PublicKey getPublicKey(String alias) {
        if (!this.containsAlias(alias)) {
            return null;
        }
        List l = this.get(alias);
        Iterator it = l.iterator();
        while (it.hasNext()) {
            Entry e = (Entry)it.next();
            if (!(e instanceof PublicKeyEntry)) continue;
            return ((PublicKeyEntry)e).getKey();
        }
        return null;
    }

    public void putPublicKey(String alias, PublicKey key) {
        if (this.containsPublicKey(alias)) {
            return;
        }
        Properties p = new Properties();
        p.put("alias", this.fixAlias(alias));
        this.add(new PublicKeyEntry(key, new Date(), p));
    }

    public boolean containsCertPath(String alias) {
        if (!this.containsAlias(alias)) {
            return false;
        }
        List l = this.get(alias);
        Iterator it = l.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof CertPathEntry)) continue;
            return true;
        }
        return false;
    }

    public Certificate[] getCertPath(String alias) {
        if (!this.containsAlias(alias)) {
            return null;
        }
        List l = this.get(alias);
        Iterator it = l.iterator();
        while (it.hasNext()) {
            Entry e = (Entry)it.next();
            if (!(e instanceof CertPathEntry)) continue;
            return ((CertPathEntry)e).getCertPath();
        }
        return null;
    }

    public void putCertPath(String alias, Certificate[] path) {
        if (this.containsCertPath(alias)) {
            return;
        }
        Properties p = new Properties();
        p.put("alias", this.fixAlias(alias));
        this.add(new CertPathEntry(path, new Date(), p));
    }

    protected void load(InputStream in, char[] password) throws IOException {
        if (in.read() != 3) {
            throw new MalformedKeyringException("incompatible keyring usage");
        }
        if (in.read() != 3) {
            throw new MalformedKeyringException("expecting password-authenticated entry tag");
        }
        this.keyring = PasswordAuthenticatedEntry.decode(new DataInputStream(in), password);
    }

    protected void store(OutputStream out, char[] password) throws IOException {
        out.write(3);
        this.keyring.encode(new DataOutputStream(out), password);
    }

    public GnuPrivateKeyring(String mac, int maclen, String cipher, String mode, int keylen) {
        this.keyring = new PasswordAuthenticatedEntry(mac, maclen, new Properties());
        this.keyring2 = new CompressedEntry(new Properties());
        this.keyring.add(this.keyring2);
        this.mac = mac;
        this.maclen = maclen;
        this.cipher = cipher;
        this.mode = mode;
        this.keylen = keylen;
    }

    public GnuPrivateKeyring() {
        this("HMAC-SHA-1", 20, "AES", "OFB", 16);
    }
}

