/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieEncoder;
import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ServerCookieEncoder
extends CookieEncoder {
    public static final ServerCookieEncoder STRICT = new ServerCookieEncoder(true);
    public static final ServerCookieEncoder LAX = new ServerCookieEncoder(false);

    private ServerCookieEncoder(boolean strict) {
        super(strict);
    }

    public String encode(String name, String value) {
        return this.encode((Cookie)new DefaultCookie(name, value));
    }

    public String encode(Cookie cookie) {
        String name = ObjectUtil.checkNotNull(cookie, "cookie").name();
        String value = cookie.value() != null ? cookie.value() : "";
        this.validateCookie(name, value);
        StringBuilder buf = CookieUtil.stringBuilder();
        if (cookie.wrap()) {
            CookieUtil.addQuoted(buf, name, value);
        } else {
            CookieUtil.add(buf, name, value);
        }
        if (cookie.maxAge() != Long.MIN_VALUE) {
            CookieUtil.add(buf, "Max-Age", cookie.maxAge());
            Date expires = new Date(cookie.maxAge() * 1000L + System.currentTimeMillis());
            buf.append("Expires");
            buf.append('=');
            DateFormatter.append(expires, buf);
            buf.append(';');
            buf.append(' ');
        }
        if (cookie.path() != null) {
            CookieUtil.add(buf, "Path", cookie.path());
        }
        if (cookie.domain() != null) {
            CookieUtil.add(buf, "Domain", cookie.domain());
        }
        if (cookie.isSecure()) {
            CookieUtil.add(buf, "Secure");
        }
        if (cookie.isHttpOnly()) {
            CookieUtil.add(buf, "HTTPOnly");
        }
        return CookieUtil.stripTrailingSeparator(buf);
    }

    private static List<String> dedup(List<String> encoded, Map<String, Integer> nameToLastIndex) {
        boolean[] isLastInstance = new boolean[encoded.size()];
        Iterator<Integer> iterator = nameToLastIndex.values().iterator();
        while (iterator.hasNext()) {
            int idx = iterator.next();
            isLastInstance[idx] = true;
        }
        ArrayList<String> dedupd = new ArrayList<String>(nameToLastIndex.size());
        int n = encoded.size();
        for (int i = 0; i < n; ++i) {
            if (!isLastInstance[i]) continue;
            dedupd.add(encoded.get(i));
        }
        return dedupd;
    }

    public /* varargs */ List<String> encode(Cookie ... cookies) {
        if (ObjectUtil.checkNotNull(cookies, "cookies").length == 0) {
            return Collections.emptyList();
        }
        ArrayList<String> encoded = new ArrayList<String>(cookies.length);
        HashMap<String, Integer> nameToIndex = this.strict && cookies.length > 1 ? new HashMap<String, Integer>() : null;
        boolean hasDupdName = false;
        for (int i = 0; i < cookies.length; ++i) {
            Cookie c = cookies[i];
            encoded.add(this.encode(c));
            if (nameToIndex == null) continue;
            hasDupdName |= nameToIndex.put(c.name(), i) != null;
        }
        return hasDupdName ? ServerCookieEncoder.dedup(encoded, nameToIndex) : encoded;
    }

    public List<String> encode(Collection<? extends Cookie> cookies) {
        if (ObjectUtil.checkNotNull(cookies, "cookies").isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> encoded = new ArrayList<String>(cookies.size());
        HashMap<String, Integer> nameToIndex = this.strict && cookies.size() > 1 ? new HashMap<String, Integer>() : null;
        int i = 0;
        boolean hasDupdName = false;
        for (Cookie c : cookies) {
            encoded.add(this.encode(c));
            if (nameToIndex == null) continue;
            hasDupdName |= nameToIndex.put(c.name(), i++) != null;
        }
        return hasDupdName ? ServerCookieEncoder.dedup(encoded, nameToIndex) : encoded;
    }

    public List<String> encode(Iterable<? extends Cookie> cookies) {
        boolean hasDupdName;
        Iterator<? extends Cookie> cookiesIt = ObjectUtil.checkNotNull(cookies, "cookies").iterator();
        if (!cookiesIt.hasNext()) {
            return Collections.emptyList();
        }
        ArrayList<String> encoded = new ArrayList<String>();
        Cookie firstCookie = cookiesIt.next();
        HashMap<String, Integer> nameToIndex = this.strict && cookiesIt.hasNext() ? new HashMap<String, Integer>() : null;
        int i = 0;
        encoded.add(this.encode(firstCookie));
        boolean bl = hasDupdName = nameToIndex != null && nameToIndex.put(firstCookie.name(), i++) != null;
        while (cookiesIt.hasNext()) {
            Cookie c = cookiesIt.next();
            encoded.add(this.encode(c));
            if (nameToIndex == null) continue;
            hasDupdName |= nameToIndex.put(c.name(), i++) != null;
        }
        return hasDupdName ? ServerCookieEncoder.dedup(encoded, nameToIndex) : encoded;
    }
}

