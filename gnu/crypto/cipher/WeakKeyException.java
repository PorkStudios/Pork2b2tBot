/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.cipher;

import java.security.InvalidKeyException;

public class WeakKeyException
extends InvalidKeyException {
    public WeakKeyException() {
    }

    public WeakKeyException(String msg) {
        super(msg);
    }
}

