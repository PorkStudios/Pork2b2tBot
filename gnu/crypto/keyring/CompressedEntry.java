/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.EnvelopeEntry;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.MeteredInputStream;
import gnu.crypto.keyring.Properties;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class CompressedEntry
extends EnvelopeEntry {
    public static final int TYPE = 4;

    public static CompressedEntry decode(DataInputStream in) throws IOException {
        CompressedEntry entry = new CompressedEntry();
        entry.properties = new Properties();
        entry.properties.decode(in);
        String alg = entry.properties.get("algorithm");
        if (alg == null) {
            throw new MalformedKeyringException("no compression algorithm");
        }
        if (!alg.equalsIgnoreCase("DEFLATE")) {
            throw new MalformedKeyringException("unsupported compression algorithm: " + alg);
        }
        int len = in.readInt();
        MeteredInputStream min = new MeteredInputStream(in, len);
        InflaterInputStream infin = new InflaterInputStream(min);
        DataInputStream in2 = new DataInputStream(infin);
        entry.decodeEnvelope(in2);
        return entry;
    }

    protected void encodePayload() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream(1024);
        DeflaterOutputStream dout = new DeflaterOutputStream(buf);
        DataOutputStream out2 = new DataOutputStream(dout);
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            ((Entry)it.next()).encode(out2);
        }
        dout.finish();
        this.payload = buf.toByteArray();
    }

    public CompressedEntry(Properties properties) {
        super(4, properties);
        this.properties.put("algorithm", "DEFLATE");
    }

    private CompressedEntry() {
    }
}

