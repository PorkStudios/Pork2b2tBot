/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.Properties;
import java.util.Date;

public abstract class PrimitiveEntry
extends Entry {
    protected Date creationDate;

    public String getAlias() {
        return this.properties.get("alias");
    }

    public Date getCreationDate() {
        return (Date)this.creationDate.clone();
    }

    public boolean equals(Object object) {
        if (!this.getClass().equals(object.getClass())) {
            return false;
        }
        return this.getAlias().equals(((PrimitiveEntry)object).getAlias());
    }

    protected final void makeCreationDate() throws MalformedKeyringException {
        String s = this.properties.get("creation-date");
        if (s == null) {
            throw new MalformedKeyringException("no creation date");
        }
        try {
            this.creationDate = new Date(Long.parseLong(s));
        }
        catch (NumberFormatException nfe) {
            throw new MalformedKeyringException("invalid creation date");
        }
    }

    protected PrimitiveEntry(int type, Date creationDate, Properties properties) {
        super(type, properties);
        this.creationDate = creationDate == null ? new Date() : (Date)creationDate.clone();
        if (!this.properties.containsKey("alias") || this.properties.get("alias").length() == 0) {
            throw new IllegalArgumentException("primitive entries MUST have an alias");
        }
        this.properties.put("creation-date", String.valueOf(creationDate.getTime()));
    }

    protected PrimitiveEntry() {
    }
}

