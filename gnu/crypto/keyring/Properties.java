/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.MeteredInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Properties
implements Cloneable {
    private HashMap props = new HashMap();

    public void clear() {
        this.props.clear();
    }

    public Object clone() {
        Properties result = new Properties();
        result.props.putAll(this.props);
        return result;
    }

    public boolean containsKey(String key) {
        if (key == null || key.length() == 0) {
            return false;
        }
        return this.props.containsKey(this.canonicalize(key));
    }

    public boolean containsValue(String value) {
        if (value == null) {
            return false;
        }
        return this.props.containsValue(value);
    }

    public String put(String key, String value) {
        if (key == null || value == null || key.length() == 0) {
            throw new IllegalArgumentException("key nor value can be null");
        }
        return this.props.put(this.canonicalize(key), value);
    }

    public String get(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        return (String)this.props.get(this.canonicalize(key));
    }

    public String remove(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        return (String)this.props.remove(this.canonicalize(key));
    }

    public void decode(DataInputStream in) throws IOException {
        int len = in.readInt();
        MeteredInputStream min = new MeteredInputStream(in, len);
        DataInputStream in2 = new DataInputStream(min);
        while (!min.limitReached()) {
            String name = in2.readUTF();
            String value = in2.readUTF();
            this.put(name, value);
        }
    }

    public void encode(DataOutputStream out) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out2 = new DataOutputStream(buf);
        Iterator it = this.props.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            out2.writeUTF((String)entry.getKey());
            out2.writeUTF((String)entry.getValue());
        }
        out.writeInt(buf.size());
        buf.writeTo(out);
    }

    public String toString() {
        return this.props.toString();
    }

    private final String canonicalize(String key) {
        return key.toLowerCase();
    }
}

