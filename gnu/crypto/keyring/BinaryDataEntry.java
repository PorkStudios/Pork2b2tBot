/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.PrimitiveEntry;
import gnu.crypto.keyring.Properties;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;

public class BinaryDataEntry
extends PrimitiveEntry {
    public static final int TYPE = 9;

    public static BinaryDataEntry decode(DataInputStream in) throws IOException {
        BinaryDataEntry entry = new BinaryDataEntry();
        entry.defaultDecode(in);
        return entry;
    }

    public String getContentType() {
        return this.properties.get("content-type");
    }

    public byte[] getData() {
        return this.getPayload();
    }

    protected void encodePayload() {
    }

    public BinaryDataEntry(String contentType, byte[] data, Date creationDate, Properties properties) {
        super(9, creationDate, properties);
        if (data == null) {
            throw new IllegalArgumentException("no data");
        }
        this.payload = (byte[])data.clone();
        if (contentType != null) {
            this.properties.put("content-type", contentType);
        }
    }

    private BinaryDataEntry() {
    }
}

