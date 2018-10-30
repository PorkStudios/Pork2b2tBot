/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.AuthenticatedEntry;
import gnu.crypto.keyring.BinaryDataEntry;
import gnu.crypto.keyring.CertPathEntry;
import gnu.crypto.keyring.CertificateEntry;
import gnu.crypto.keyring.CompressedEntry;
import gnu.crypto.keyring.EncryptedEntry;
import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.MaskableEnvelopeEntry;
import gnu.crypto.keyring.PasswordAuthenticatedEntry;
import gnu.crypto.keyring.PasswordEncryptedEntry;
import gnu.crypto.keyring.PrimitiveEntry;
import gnu.crypto.keyring.PrivateKeyEntry;
import gnu.crypto.keyring.Properties;
import gnu.crypto.keyring.PublicKeyEntry;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class EnvelopeEntry
extends Entry {
    protected EnvelopeEntry containingEnvelope;
    protected List entries = new LinkedList();

    public void add(Entry entry) {
        if (!this.containsEntry(entry)) {
            if (entry instanceof EnvelopeEntry) {
                ((EnvelopeEntry)entry).setContainingEnvelope(this);
            }
            this.entries.add(entry);
            this.payload = null;
            this.makeAliasList();
        }
    }

    public boolean containsAlias(String alias) {
        String aliases = this.getAliasList();
        if (aliases == null) {
            return false;
        }
        StringTokenizer tok = new StringTokenizer(aliases, ";");
        while (tok.hasMoreTokens()) {
            if (!tok.nextToken().equals(alias)) continue;
            return true;
        }
        return false;
    }

    public boolean containsEntry(Entry entry) {
        if (entry instanceof EnvelopeEntry) {
            return this.entries.contains(entry);
        }
        if (entry instanceof PrimitiveEntry) {
            Iterator it = this.entries.iterator();
            while (it.hasNext()) {
                Entry e = (Entry)it.next();
                if (e.equals(entry)) {
                    return true;
                }
                if (!(e instanceof EnvelopeEntry) || !((EnvelopeEntry)e).containsEntry(entry)) continue;
                return true;
            }
        }
        return false;
    }

    public List getEntries() {
        return new ArrayList(this.entries);
    }

    public List get(String alias) {
        LinkedList<Entry> result = new LinkedList<Entry>();
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            Entry e = (Entry)it.next();
            if (e instanceof EnvelopeEntry) {
                if (!((EnvelopeEntry)e).containsAlias(alias)) continue;
                if (e instanceof MaskableEnvelopeEntry && ((MaskableEnvelopeEntry)e).isMasked()) {
                    result.add(e);
                    continue;
                }
                result.addAll(((EnvelopeEntry)e).get(alias));
                continue;
            }
            if (!(e instanceof PrimitiveEntry) || !((PrimitiveEntry)e).getAlias().equals(alias)) continue;
            result.add(e);
        }
        return result;
    }

    public String getAliasList() {
        String list = this.properties.get("alias-list");
        if (list == null) {
            return "";
        }
        return list;
    }

    public boolean remove(Entry entry) {
        boolean ret = false;
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            Entry e = (Entry)it.next();
            if (e instanceof EnvelopeEntry) {
                if (e == entry) {
                    it.remove();
                    ret = true;
                    break;
                }
                if (!((EnvelopeEntry)e).remove(entry)) continue;
                ret = true;
                break;
            }
            if (!(e instanceof PrimitiveEntry) || !((PrimitiveEntry)e).equals(entry)) continue;
            it.remove();
            ret = true;
            break;
        }
        if (ret) {
            this.payload = null;
            this.makeAliasList();
        }
        return ret;
    }

    public void remove(String alias) {
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            Entry e = (Entry)it.next();
            if (e instanceof EnvelopeEntry) {
                ((EnvelopeEntry)e).remove(alias);
                continue;
            }
            if (!(e instanceof PrimitiveEntry) || !((PrimitiveEntry)e).getAlias().equals(alias)) continue;
            it.remove();
        }
        this.payload = null;
        this.makeAliasList();
    }

    protected void encodePayload() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
        DataOutputStream out = new DataOutputStream(bout);
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            ((Entry)it.next()).encode(out);
        }
    }

    protected void setContainingEnvelope(EnvelopeEntry e) {
        if (this.containingEnvelope != null) {
            throw new IllegalArgumentException("envelopes may not be shared");
        }
        this.containingEnvelope = e;
    }

    protected void decodeEnvelope(DataInputStream in) throws IOException {
        int type;
        block13 : do {
            type = in.read();
            switch (type) {
                case 0: {
                    this.add(EncryptedEntry.decode(in));
                    continue block13;
                }
                case 1: {
                    this.add(PasswordEncryptedEntry.decode(in));
                    continue block13;
                }
                case 3: {
                    this.add(PasswordAuthenticatedEntry.decode(in));
                    continue block13;
                }
                case 2: {
                    this.add(AuthenticatedEntry.decode(in));
                    continue block13;
                }
                case 4: {
                    this.add(CompressedEntry.decode(in));
                    continue block13;
                }
                case 5: {
                    this.add(CertificateEntry.decode(in));
                    continue block13;
                }
                case 6: {
                    this.add(PublicKeyEntry.decode(in));
                    continue block13;
                }
                case 7: {
                    this.add(PrivateKeyEntry.decode(in));
                    continue block13;
                }
                case 8: {
                    this.add(CertPathEntry.decode(in));
                    continue block13;
                }
                case 9: {
                    this.add(BinaryDataEntry.decode(in));
                    continue block13;
                }
                case -1: {
                    return;
                }
            }
            break;
        } while (true);
        throw new MalformedKeyringException("unknown type " + type);
    }

    private final void makeAliasList() {
        if (this.entries.isEmpty()) {
            return;
        }
        StringBuffer buf = new StringBuffer();
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry)it.next();
            if (entry instanceof EnvelopeEntry) {
                buf.append(((EnvelopeEntry)entry).getAliasList());
            } else if (entry instanceof PrimitiveEntry) {
                buf.append(((PrimitiveEntry)entry).getAlias());
            }
            if (!it.hasNext()) continue;
            buf.append(';');
        }
        this.properties.put("alias-list", buf.toString());
        if (this.containingEnvelope != null) {
            this.containingEnvelope.makeAliasList();
        }
    }

    public EnvelopeEntry(int type, Properties properties) {
        super(type, properties);
        if (this.properties.get("alias-list") != null) {
            this.properties.remove("alias-list");
        }
    }

    protected EnvelopeEntry() {
    }
}

