/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.Registry;
import gnu.crypto.keyring.CompressedEntry;
import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.IKeyring;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.PasswordAuthenticatedEntry;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class BaseKeyring
implements IKeyring {
    private static final IllegalStateException NOT_LOADED = new IllegalStateException("keyring not loaded");
    protected PasswordAuthenticatedEntry keyring;
    protected CompressedEntry keyring2;

    public void load(Map attributes) throws IOException {
        InputStream in = (InputStream)attributes.get("gnu.crypto.keyring.data.in");
        if (in == null) {
            throw new IllegalArgumentException("no input stream");
        }
        char[] password = (char[])attributes.get("gnu.crypto.keyring.password");
        if (password == null) {
            password = new char[]{};
        }
        if (in.read() != Registry.GKR_MAGIC[0] || in.read() != Registry.GKR_MAGIC[1] || in.read() != Registry.GKR_MAGIC[2] || in.read() != Registry.GKR_MAGIC[3]) {
            throw new MalformedKeyringException("magic");
        }
        this.load(in, password);
        List l = this.keyring.getEntries();
        if (l.size() == 1 && l.get(0) instanceof CompressedEntry) {
            this.keyring2 = (CompressedEntry)l.get(0);
        }
    }

    public void store(Map attributes) throws IOException {
        OutputStream out = (OutputStream)attributes.get("gun.crypto.keyring.data.out");
        if (out == null) {
            throw new IllegalArgumentException("no output stream");
        }
        char[] password = (char[])attributes.get("gnu.crypto.keyring.password");
        if (password == null) {
            password = new char[]{};
        }
        if (this.keyring == null) {
            throw new IllegalStateException("empty keyring");
        }
        out.write(Registry.GKR_MAGIC);
        this.store(out, password);
    }

    public void reset() {
        this.keyring = null;
    }

    public int size() {
        if (this.keyring == null) {
            throw NOT_LOADED;
        }
        return ((StringTokenizer)this.aliases()).countTokens();
    }

    public Enumeration aliases() {
        if (this.keyring == null) {
            throw NOT_LOADED;
        }
        return new StringTokenizer(this.keyring.getAliasList(), ";");
    }

    public boolean containsAlias(String alias) {
        if (this.keyring == null) {
            throw new IllegalStateException("keyring not loaded");
        }
        return this.keyring.containsAlias(alias);
    }

    public List get(String alias) {
        if (this.keyring == null) {
            throw new IllegalStateException("keyring not loaded");
        }
        return this.keyring.get(alias);
    }

    public void add(Entry entry) {
        if (this.keyring == null) {
            throw new IllegalStateException("keyring not loaded");
        }
        if (this.keyring2 != null) {
            this.keyring2.add(entry);
        } else {
            this.keyring.add(entry);
        }
    }

    public void remove(String alias) {
        if (this.keyring == null) {
            throw new IllegalStateException("keyring not loaded");
        }
        this.keyring.remove(alias);
    }

    protected String fixAlias(String alias) {
        return alias.replace(';', '_');
    }

    protected abstract void load(InputStream var1, char[] var2) throws IOException;

    protected abstract void store(OutputStream var1, char[] var2) throws IOException;
}

