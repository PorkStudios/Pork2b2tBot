/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import javax.security.sasl.SaslException;

public class IntegrityException
extends SaslException {
    public IntegrityException() {
    }

    public IntegrityException(String s) {
        super(s);
    }

    public IntegrityException(String s, Throwable x) {
        super(s, x);
    }
}

