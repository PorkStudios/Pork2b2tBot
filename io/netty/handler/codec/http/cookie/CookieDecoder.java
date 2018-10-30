/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.CharBuffer;

public abstract class CookieDecoder {
    private final InternalLogger logger = InternalLoggerFactory.getInstance(this.getClass());
    private final boolean strict;

    protected CookieDecoder(boolean strict) {
        this.strict = strict;
    }

    protected DefaultCookie initCookie(String header, int nameBegin, int nameEnd, int valueBegin, int valueEnd) {
        int invalidOctetPos;
        boolean wrap;
        if (nameBegin == -1 || nameBegin == nameEnd) {
            this.logger.debug("Skipping cookie with null name");
            return null;
        }
        if (valueBegin == -1) {
            this.logger.debug("Skipping cookie with null value");
            return null;
        }
        CharBuffer wrappedValue = CharBuffer.wrap(header, valueBegin, valueEnd);
        CharSequence unwrappedValue = CookieUtil.unwrapValue(wrappedValue);
        if (unwrappedValue == null) {
            this.logger.debug("Skipping cookie because starting quotes are not properly balanced in '{}'", (Object)wrappedValue);
            return null;
        }
        String name = header.substring(nameBegin, nameEnd);
        if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieNameOctet(name)) >= 0) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Skipping cookie because name '{}' contains invalid char '{}'", (Object)name, (Object)Character.valueOf(name.charAt(invalidOctetPos)));
            }
            return null;
        }
        boolean bl = wrap = unwrappedValue.length() != valueEnd - valueBegin;
        if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieValueOctet(unwrappedValue)) >= 0) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Skipping cookie because value '{}' contains invalid char '{}'", (Object)unwrappedValue, (Object)Character.valueOf(unwrappedValue.charAt(invalidOctetPos)));
            }
            return null;
        }
        DefaultCookie cookie = new DefaultCookie(name, unwrappedValue.toString());
        cookie.setWrap(wrap);
        return cookie;
    }
}

