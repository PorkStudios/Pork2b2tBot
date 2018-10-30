/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.Properties;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Entry {
    protected int type;
    protected Properties properties;
    protected byte[] payload;

    public Properties getProperties() {
        return (Properties)this.properties.clone();
    }

    public byte[] getPayload() {
        if (this.payload == null) {
            return null;
        }
        return (byte[])this.payload.clone();
    }

    public void encode(DataOutputStream out) throws IOException {
        if (this.payload == null) {
            this.encodePayload();
        }
        if (out == null) {
            return;
        }
        out.write(this.type);
        this.properties.encode(out);
        out.writeInt(this.payload.length);
        out.write(this.payload);
    }

    protected void defaultDecode(DataInputStream in) throws IOException {
        this.properties = new Properties();
        this.properties.decode(in);
        int len = in.readInt();
        if (len < 0) {
            throw new IOException("corrupt length");
        }
        this.payload = new byte[len];
        in.readFully(this.payload);
    }

    protected abstract void encodePayload() throws IOException;

    protected Entry(int type, Properties properties) {
        if (type < 0 || type > 255) {
            throw new IllegalArgumentException("invalid packet type");
        }
        if (properties == null) {
            throw new IllegalArgumentException("no properties");
        }
        this.type = type;
        this.properties = (Properties)properties.clone();
    }

    protected Entry() {
    }
}

