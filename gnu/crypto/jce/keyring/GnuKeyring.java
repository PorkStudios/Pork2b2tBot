/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.keyring;

import gnu.crypto.Registry;
import gnu.crypto.jce.keyring.GnuKeyring;
import gnu.crypto.keyring.GnuPrivateKeyring;
import gnu.crypto.keyring.GnuPublicKeyring;
import gnu.crypto.keyring.IKeyring;
import gnu.crypto.keyring.IPrivateKeyring;
import gnu.crypto.keyring.IPublicKeyring;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.PrimitiveEntry;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;

public class GnuKeyring
extends KeyStoreSpi {
    private static final IllegalStateException NOT_LOADED = new IllegalStateException("keyring not loaded");
    private boolean loaded;
    private IKeyring keyring;

    public Enumeration engineAliases() {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return new Enumeration(this){
                final /* synthetic */ GnuKeyring this$0;

                public final boolean hasMoreElements() {
                    return false;
                }

                public final Object nextElement() {
                    throw new java.util.NoSuchElementException();
                }
                {
                    this.this$0 = gnuKeyring;
                }
            };
        }
        return this.keyring.aliases();
    }

    public boolean engineContainsAlias(String alias) {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return false;
        }
        return this.keyring.containsAlias(alias);
    }

    public void engineDeleteEntry(String alias) {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring != null) {
            this.keyring.remove(alias);
        }
    }

    public Certificate engineGetCertificate(String alias) {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return null;
        }
        if (!(this.keyring instanceof IPublicKeyring)) {
            throw new IllegalStateException("not a public keyring");
        }
        return ((IPublicKeyring)this.keyring).getCertificate(alias);
    }

    public String engineGetCertificateAlias(Certificate cert) {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return null;
        }
        if (!(this.keyring instanceof IPublicKeyring)) {
            throw new IllegalStateException("not a public keyring");
        }
        Enumeration aliases = this.keyring.aliases();
        while (aliases.hasMoreElements()) {
            String alias = (String)aliases.nextElement();
            Certificate cert2 = ((IPublicKeyring)this.keyring).getCertificate(alias);
            if (!cert.equals(cert2)) continue;
            return alias;
        }
        return null;
    }

    public void engineSetCertificateEntry(String alias, Certificate cert) {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            this.keyring = new GnuPublicKeyring("HMAC-SHA-1", 20);
        }
        if (!(this.keyring instanceof IPublicKeyring)) {
            throw new IllegalStateException("not a public keyring");
        }
        ((IPublicKeyring)this.keyring).putCertificate(alias, cert);
    }

    public Certificate[] engineGetCertificateChain(String alias) {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return null;
        }
        if (!(this.keyring instanceof IPrivateKeyring)) {
            throw new IllegalStateException("not a private keyring");
        }
        return ((IPrivateKeyring)this.keyring).getCertPath(alias);
    }

    public Date engineGetCreationDate(String alias) {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return null;
        }
        List entries = this.keyring.get(alias);
        if (entries.size() == 0) {
            return null;
        }
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!(o instanceof PrimitiveEntry)) continue;
            return ((PrimitiveEntry)o).getCreationDate();
        }
        return null;
    }

    public Key engineGetKey(String alias, char[] password) throws UnrecoverableKeyException {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return null;
        }
        if (!(this.keyring instanceof IPrivateKeyring)) {
            throw new IllegalStateException("not a private keyring");
        }
        if (password == null && ((IPrivateKeyring)this.keyring).containsPublicKey(alias)) {
            return ((IPrivateKeyring)this.keyring).getPublicKey(alias);
        }
        if (((IPrivateKeyring)this.keyring).containsPrivateKey(alias)) {
            return ((IPrivateKeyring)this.keyring).getPrivateKey(alias, password);
        }
        return null;
    }

    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            this.keyring = new GnuPrivateKeyring("HMAC-SHA-1", 20, "AES", "OFB", 16);
        }
        if (!(this.keyring instanceof IPrivateKeyring)) {
            throw new IllegalStateException("not a private keyring");
        }
        if (key instanceof PublicKey) {
            ((IPrivateKeyring)this.keyring).putPublicKey(alias, (PublicKey)key);
            return;
        }
        if (!(key instanceof PrivateKey) && !(key instanceof SecretKey)) {
            throw new KeyStoreException("cannot store keys of type " + key.getClass().getName());
        }
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            ((IPrivateKeyring)this.keyring).putCertPath(alias, chain);
        }
        catch (CertificateException ce) {
            throw new KeyStoreException(ce.toString());
        }
        ((IPrivateKeyring)this.keyring).putPrivateKey(alias, key, password);
    }

    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
        throw new KeyStoreException("method not supported");
    }

    public boolean engineIsCertificateEntry(String alias) {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return false;
        }
        if (!(this.keyring instanceof IPublicKeyring)) {
            return false;
        }
        return ((IPublicKeyring)this.keyring).containsCertificate(alias);
    }

    public boolean engineIsKeyEntry(String alias) {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return false;
        }
        if (!(this.keyring instanceof IPrivateKeyring)) {
            return false;
        }
        boolean bl = false;
        if (((IPrivateKeyring)this.keyring).containsPublicKey(alias) || ((IPrivateKeyring)this.keyring).containsPrivateKey(alias)) {
            bl = true;
        }
        return bl;
    }

    public void engineLoad(InputStream in, char[] password) throws IOException {
        if (in != null) {
            if (!in.markSupported()) {
                in = new BufferedInputStream(in);
            }
            in.mark(5);
            int i = 0;
            while (i < 4) {
                if (in.read() != Registry.GKR_MAGIC[i]) {
                    throw new MalformedKeyringException("incorrect magic");
                }
                ++i;
            }
            int usage = in.read();
            in.reset();
            HashMap<String, InputStream> attr = new HashMap<String, InputStream>();
            attr.put("gnu.crypto.keyring.data.in", in);
            attr.put("gnu.crypto.keyring.password", (InputStream)password);
            switch (usage) {
                case 1: {
                    this.keyring = new GnuPublicKeyring();
                    break;
                }
                case 3: {
                    this.keyring = new GnuPrivateKeyring();
                    break;
                }
                default: {
                    throw new MalformedKeyringException("unsupported ring usage");
                }
            }
            this.keyring.load(attr);
        }
        this.loaded = true;
    }

    public void engineStore(OutputStream out, char[] password) throws IOException {
        if (!this.loaded || this.keyring == null) {
            throw NOT_LOADED;
        }
        HashMap<String, OutputStream> attr = new HashMap<String, OutputStream>();
        attr.put("gun.crypto.keyring.data.out", out);
        attr.put("gnu.crypto.keyring.password", (OutputStream)password);
        this.keyring.store(attr);
    }

    public int engineSize() {
        if (!this.loaded) {
            throw NOT_LOADED;
        }
        if (this.keyring == null) {
            return 0;
        }
        return this.keyring.size();
    }
}

