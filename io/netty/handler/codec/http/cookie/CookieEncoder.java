/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.CookieUtil;

public abstract class CookieEncoder {
    protected final boolean strict;

    protected CookieEncoder(boolean strict) {
        this.strict = strict;
    }

    protected void validateCookie(String name, String value) {
        if (this.strict) {
            int pos = CookieUtil.firstInvalidCookieNameOctet(name);
            if (pos >= 0) {
                throw new IllegalArgumentException("Cookie name contains an invalid char: " + name.charAt(pos));
            }
            CharSequence unwrappedValue = CookieUtil.unwrapValue(value);
            if (unwrappedValue == null) {
                throw new IllegalArgumentException("Cookie value wrapping quotes are not balanced: " + value);
            }
            pos = CookieUtil.firstInvalidCookieValueOctet(unwrappedValue);
            if (pos >= 0) {
                throw new IllegalArgumentException("Cookie value contains an invalid char: " + value.charAt(pos));
            }
        }
    }
}

