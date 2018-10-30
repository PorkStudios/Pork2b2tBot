/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import java.security.InvalidKeyException;
import java.util.Map;

public interface IMac
extends Cloneable {
    public static final String MAC_KEY_MATERIAL = "gnu.crypto.mac.key.material";
    public static final String TRUNCATED_SIZE = "gnu.crypto.mac.truncated.size";

    public String name();

    public int macSize();

    public void init(Map var1) throws InvalidKeyException, IllegalStateException;

    public void update(byte var1);

    public void update(byte[] var1, int var2, int var3);

    public byte[] digest();

    public void reset();

    public boolean selfTest();

    public Object clone();
}

