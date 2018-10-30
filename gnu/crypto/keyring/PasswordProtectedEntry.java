/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import java.io.DataOutputStream;
import java.io.IOException;

public interface PasswordProtectedEntry {
    public static final Integer ITERATION_COUNT = new Integer(1000);

    public void encode(DataOutputStream var1, char[] var2) throws IOException;
}

