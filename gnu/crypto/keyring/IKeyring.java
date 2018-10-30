/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.Entry;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public interface IKeyring {
    public static final String KEYRING_DATA_IN = "gnu.crypto.keyring.data.in";
    public static final String KEYRING_DATA_OUT = "gun.crypto.keyring.data.out";
    public static final String KEYRING_PASSWORD = "gnu.crypto.keyring.password";

    public void load(Map var1) throws IOException;

    public void store(Map var1) throws IOException;

    public void reset();

    public int size();

    public Enumeration aliases();

    public boolean containsAlias(String var1);

    public List get(String var1);

    public void add(Entry var1);

    public void remove(String var1);
}

