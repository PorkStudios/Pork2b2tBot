/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.BaseKeyring;
import gnu.crypto.keyring.CertificateEntry;
import gnu.crypto.keyring.CompressedEntry;
import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.IPublicKeyring;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.PasswordAuthenticatedEntry;
import gnu.crypto.keyring.Properties;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class GnuPublicKeyring
extends BaseKeyring
implements IPublicKeyring {
    public static final int USAGE = 1;

    public boolean containsCertificate(String alias) {
        if (!this.containsAlias(alias)) {
            return false;
        }
        List l = this.get(alias);
        Iterator it = l.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof CertificateEntry)) continue;
            return true;
        }
        return false;
    }

    public Certificate getCertificate(String alias) {
        if (!this.containsAlias(alias)) {
            return null;
        }
        List l = this.get(alias);
        Iterator it = l.iterator();
        while (it.hasNext()) {
            Entry e = (Entry)it.next();
            if (!(e instanceof CertificateEntry)) continue;
            return ((CertificateEntry)e).getCertificate();
        }
        return null;
    }

    public void putCertificate(String alias, Certificate cert) {
        if (this.containsCertificate(alias)) {
            return;
        }
        Properties p = new Properties();
        p.put("alias", this.fixAlias(alias));
        this.add(new CertificateEntry(cert, new Date(), p));
    }

    protected void load(InputStream in, char[] password) throws IOException {
        if (in.read() != 1) {
            throw new MalformedKeyringException("incompatible keyring usage");
        }
        if (in.read() != 3) {
            throw new MalformedKeyringException("expecting password-authenticated entry tag");
        }
        this.keyring = PasswordAuthenticatedEntry.decode(new DataInputStream(in), password);
    }

    protected void store(OutputStream out, char[] password) throws IOException {
        out.write(1);
        this.keyring.encode(new DataOutputStream(out), password);
    }

    public GnuPublicKeyring(String mac, int macLen) {
        this.keyring = new PasswordAuthenticatedEntry(mac, macLen, new Properties());
        this.keyring2 = new CompressedEntry(new Properties());
        this.keyring.add(this.keyring2);
    }

    public GnuPublicKeyring() {
    }
}

