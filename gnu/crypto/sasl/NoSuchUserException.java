/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import javax.security.sasl.AuthenticationException;

public class NoSuchUserException
extends AuthenticationException {
    public NoSuchUserException() {
    }

    public NoSuchUserException(String arg) {
        super(arg);
    }
}

