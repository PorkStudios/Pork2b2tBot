/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import javax.security.sasl.SaslException;

public class ConfidentialityException
extends SaslException {
    public ConfidentialityException() {
    }

    public ConfidentialityException(String s) {
        super(s);
    }

    public ConfidentialityException(String s, Throwable x) {
        super(s, x);
    }
}

