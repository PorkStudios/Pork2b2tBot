/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieEncoder;
import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public final class ClientCookieEncoder
extends CookieEncoder {
    public static final ClientCookieEncoder STRICT = new ClientCookieEncoder(true);
    public static final ClientCookieEncoder LAX = new ClientCookieEncoder(false);
    private static final Comparator<Cookie> COOKIE_COMPARATOR = new Comparator<Cookie>(){

        @Override
        public int compare(Cookie c1, Cookie c2) {
            int len1;
            String path1 = c1.path();
            String path2 = c2.path();
            int len2 = path2 == null ? Integer.MAX_VALUE : path2.length();
            int diff = len2 - (len1 = path1 == null ? Integer.MAX_VALUE : path1.length());
            if (diff != 0) {
                return diff;
            }
            return -1;
        }
    };

    private ClientCookieEncoder(boolean strict) {
        super(strict);
    }

    public String encode(String name, String value) {
        return this.encode((Cookie)new DefaultCookie(name, value));
    }

    public String encode(Cookie cookie) {
        StringBuilder buf = CookieUtil.stringBuilder();
        this.encode(buf, ObjectUtil.checkNotNull(cookie, "cookie"));
        return CookieUtil.stripTrailingSeparator(buf);
    }

    public /* varargs */ String encode(Cookie ... cookies) {
        StringBuilder buf;
        if (ObjectUtil.checkNotNull(cookies, "cookies").length == 0) {
            return null;
        }
        buf = CookieUtil.stringBuilder();
        if (this.strict) {
            if (cookies.length == 1) {
                this.encode(buf, cookies[0]);
            } else {
                Cookie[] cookiesSorted = Arrays.copyOf(cookies, cookies.length);
                Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
                for (Cookie c : cookiesSorted) {
                    this.encode(buf, c);
                }
            }
        } else {
            for (Cookie c : cookies) {
                this.encode(buf, c);
            }
        }
        return CookieUtil.stripTrailingSeparatorOrNull(buf);
    }

    public String encode(Collection<? extends Cookie> cookies) {
        StringBuilder buf;
        if (ObjectUtil.checkNotNull(cookies, "cookies").isEmpty()) {
            return null;
        }
        buf = CookieUtil.stringBuilder();
        if (this.strict) {
            if (cookies.size() == 1) {
                this.encode(buf, cookies.iterator().next());
            } else {
                Cookie[] cookiesSorted = cookies.toArray(new Cookie[cookies.size()]);
                Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
                for (Cookie c : cookiesSorted) {
                    this.encode(buf, c);
                }
            }
        } else {
            for (Cookie c : cookies) {
                this.encode(buf, c);
            }
        }
        return CookieUtil.stripTrailingSeparatorOrNull(buf);
    }

    public String encode(Iterable<? extends Cookie> cookies) {
        StringBuilder buf;
        Iterator<? extends Cookie> cookiesIt = ObjectUtil.checkNotNull(cookies, "cookies").iterator();
        if (!cookiesIt.hasNext()) {
            return null;
        }
        buf = CookieUtil.stringBuilder();
        if (this.strict) {
            Cookie firstCookie = cookiesIt.next();
            if (!cookiesIt.hasNext()) {
                this.encode(buf, firstCookie);
            } else {
                ArrayList<Cookie> cookiesList = InternalThreadLocalMap.get().arrayList();
                cookiesList.add(firstCookie);
                while (cookiesIt.hasNext()) {
                    cookiesList.add(cookiesIt.next());
                }
                Cookie[] cookiesSorted = cookiesList.toArray(new Cookie[cookiesList.size()]);
                Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
                for (Cookie c : cookiesSorted) {
                    this.encode(buf, c);
                }
            }
        } else {
            while (cookiesIt.hasNext()) {
                this.encode(buf, cookiesIt.next());
            }
        }
        return CookieUtil.stripTrailingSeparatorOrNull(buf);
    }

    private void encode(StringBuilder buf, Cookie c) {
        String name = c.name();
        String value = c.value() != null ? c.value() : "";
        this.validateCookie(name, value);
        if (c.wrap()) {
            CookieUtil.addQuoted(buf, name, value);
        } else {
            CookieUtil.add(buf, name, value);
        }
    }

}

