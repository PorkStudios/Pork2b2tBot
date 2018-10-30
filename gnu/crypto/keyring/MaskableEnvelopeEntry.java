/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.EnvelopeEntry;
import gnu.crypto.keyring.Properties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MaskableEnvelopeEntry
extends EnvelopeEntry {
    protected boolean masked;

    protected final void setMasked(boolean masked) {
        this.masked = masked;
    }

    public boolean isMasked() {
        return this.masked;
    }

    public void add(Entry entry) {
        if (this.isMasked()) {
            throw new IllegalStateException("masked envelope");
        }
        super.add(entry);
    }

    public boolean containsEntry(Entry entry) {
        if (this.isMasked()) {
            throw new IllegalStateException("masked envelope");
        }
        return super.containsEntry(entry);
    }

    public List getEntries() {
        if (this.isMasked()) {
            throw new IllegalStateException("masked envelope");
        }
        return new ArrayList(this.entries);
    }

    public List get(String alias) {
        if (this.isMasked()) {
            throw new IllegalStateException("masked envelope");
        }
        return super.get(alias);
    }

    public boolean remove(Entry entry) {
        if (this.isMasked()) {
            throw new IllegalStateException("masked envelope");
        }
        return super.remove(entry);
    }

    public void remove(String alias) {
        if (this.isMasked()) {
            throw new IllegalStateException("masked envelope");
        }
        super.remove(alias);
    }

    public MaskableEnvelopeEntry(int type, Properties properties) {
        super(type, properties);
    }

    protected MaskableEnvelopeEntry() {
    }
}

