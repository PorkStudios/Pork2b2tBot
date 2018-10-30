/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieDecoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.ObjectUtil;
import java.util.Date;

public final class ClientCookieDecoder
extends CookieDecoder {
    public static final ClientCookieDecoder STRICT = new ClientCookieDecoder(true);
    public static final ClientCookieDecoder LAX = new ClientCookieDecoder(false);

    private ClientCookieDecoder(boolean strict) {
        super(strict);
    }

    public Cookie decode(String header) {
        char c;
        int headerLen = ObjectUtil.checkNotNull(header, "header").length();
        if (headerLen == 0) {
            return null;
        }
        CookieBuilder cookieBuilder = null;
        int i = 0;
        while (i != headerLen && (c = header.charAt(i)) != ',') {
            int valueEnd;
            int nameEnd;
            int valueBegin;
            int nameBegin;
            block11 : {
                if (c == '\t' || c == '\n' || c == '\u000b' || c == '\f' || c == '\r' || c == ' ' || c == ';') {
                    ++i;
                    continue;
                }
                nameBegin = i;
                do {
                    char curChar;
                    if ((curChar = header.charAt(i)) == ';') {
                        nameEnd = i;
                        valueEnd = -1;
                        valueBegin = -1;
                    } else {
                        if (curChar != '=') continue;
                        nameEnd = i++;
                        if (i == headerLen) {
                            valueEnd = 0;
                            valueBegin = 0;
                        } else {
                            valueBegin = i;
                            int semiPos = header.indexOf(59, i);
                            i = semiPos > 0 ? semiPos : headerLen;
                            valueEnd = i;
                        }
                    }
                    break block11;
                } while (++i != headerLen);
                nameEnd = headerLen;
                valueEnd = -1;
                valueBegin = -1;
            }
            if (valueEnd > 0 && header.charAt(valueEnd - 1) == ',') {
                --valueEnd;
            }
            if (cookieBuilder == null) {
                DefaultCookie cookie = this.initCookie(header, nameBegin, nameEnd, valueBegin, valueEnd);
                if (cookie == null) {
                    return null;
                }
                cookieBuilder = new CookieBuilder(cookie, header);
                continue;
            }
            cookieBuilder.appendAttribute(nameBegin, nameEnd, valueBegin, valueEnd);
        }
        return cookieBuilder.cookie();
    }

    private static class CookieBuilder {
        private final String header;
        private final DefaultCookie cookie;
        private String domain;
        private String path;
        private long maxAge = Long.MIN_VALUE;
        private int expiresStart;
        private int expiresEnd;
        private boolean secure;
        private boolean httpOnly;

        CookieBuilder(DefaultCookie cookie, String header) {
            this.cookie = cookie;
            this.header = header;
        }

        private long mergeMaxAgeAndExpires() {
            Date expiresDate;
            if (this.maxAge != Long.MIN_VALUE) {
                return this.maxAge;
            }
            if (CookieBuilder.isValueDefined(this.expiresStart, this.expiresEnd) && (expiresDate = DateFormatter.parseHttpDate(this.header, this.expiresStart, this.expiresEnd)) != null) {
                long maxAgeMillis;
                return maxAgeMillis / 1000L + (long)((maxAgeMillis = expiresDate.getTime() - System.currentTimeMillis()) % 1000L != 0L ? 1 : 0);
            }
            return Long.MIN_VALUE;
        }

        Cookie cookie() {
            this.cookie.setDomain(this.domain);
            this.cookie.setPath(this.path);
            this.cookie.setMaxAge(this.mergeMaxAgeAndExpires());
            this.cookie.setSecure(this.secure);
            this.cookie.setHttpOnly(this.httpOnly);
            return this.cookie;
        }

        void appendAttribute(int keyStart, int keyEnd, int valueStart, int valueEnd) {
            int length = keyEnd - keyStart;
            if (length == 4) {
                this.parse4(keyStart, valueStart, valueEnd);
            } else if (length == 6) {
                this.parse6(keyStart, valueStart, valueEnd);
            } else if (length == 7) {
                this.parse7(keyStart, valueStart, valueEnd);
            } else if (length == 8) {
                this.parse8(keyStart);
            }
        }

        private void parse4(int nameStart, int valueStart, int valueEnd) {
            if (this.header.regionMatches(true, nameStart, "Path", 0, 4)) {
                this.path = this.computeValue(valueStart, valueEnd);
            }
        }

        private void parse6(int nameStart, int valueStart, int valueEnd) {
            if (this.header.regionMatches(true, nameStart, "Domain", 0, 5)) {
                this.domain = this.computeValue(valueStart, valueEnd);
            } else if (this.header.regionMatches(true, nameStart, "Secure", 0, 5)) {
                this.secure = true;
            }
        }

        private void setMaxAge(String value) {
            try {
                this.maxAge = Math.max(Long.parseLong(value), 0L);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }

        private void parse7(int nameStart, int valueStart, int valueEnd) {
            if (this.header.regionMatches(true, nameStart, "Expires", 0, 7)) {
                this.expiresStart = valueStart;
                this.expiresEnd = valueEnd;
            } else if (this.header.regionMatches(true, nameStart, "Max-Age", 0, 7)) {
                this.setMaxAge(this.computeValue(valueStart, valueEnd));
            }
        }

        private void parse8(int nameStart) {
            if (this.header.regionMatches(true, nameStart, "HTTPOnly", 0, 8)) {
                this.httpOnly = true;
            }
        }

        private static boolean isValueDefined(int valueStart, int valueEnd) {
            return valueStart != -1 && valueStart != valueEnd;
        }

        private String computeValue(int valueStart, int valueEnd) {
            return CookieBuilder.isValueDefined(valueStart, valueEnd) ? this.header.substring(valueStart, valueEnd) : null;
        }
    }

}

