/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryStringDecoder {
    private static final int DEFAULT_MAX_PARAMS = 1024;
    private final Charset charset;
    private final String uri;
    private final int maxParams;
    private int pathEndIdx;
    private String path;
    private Map<String, List<String>> params;

    public QueryStringDecoder(String uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(String uri, boolean hasPath) {
        this(uri, HttpConstants.DEFAULT_CHARSET, hasPath);
    }

    public QueryStringDecoder(String uri, Charset charset) {
        this(uri, charset, true);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath) {
        this(uri, charset, hasPath, 1024);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams) {
        this.uri = ObjectUtil.checkNotNull(uri, "uri");
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
        this.maxParams = ObjectUtil.checkPositive(maxParams, "maxParams");
        this.pathEndIdx = hasPath ? -1 : 0;
    }

    public QueryStringDecoder(URI uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringDecoder(URI uri, Charset charset) {
        this(uri, charset, 1024);
    }

    public QueryStringDecoder(URI uri, Charset charset, int maxParams) {
        String rawQuery;
        String rawPath = uri.getRawPath();
        if (rawPath == null) {
            rawPath = "";
        }
        this.uri = (rawQuery = uri.getRawQuery()) == null ? rawPath : rawPath + '?' + rawQuery;
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
        this.maxParams = ObjectUtil.checkPositive(maxParams, "maxParams");
        this.pathEndIdx = rawPath.length();
    }

    public String toString() {
        return this.uri();
    }

    public String uri() {
        return this.uri;
    }

    public String path() {
        if (this.path == null) {
            this.path = QueryStringDecoder.decodeComponent(this.uri, 0, this.pathEndIdx(), this.charset, true);
        }
        return this.path;
    }

    public Map<String, List<String>> parameters() {
        if (this.params == null) {
            this.params = QueryStringDecoder.decodeParams(this.uri, this.pathEndIdx(), this.charset, this.maxParams);
        }
        return this.params;
    }

    public String rawPath() {
        return this.uri.substring(0, this.pathEndIdx());
    }

    public String rawQuery() {
        int start = this.pathEndIdx() + 1;
        return start < this.uri.length() ? this.uri.substring(start) : "";
    }

    private int pathEndIdx() {
        if (this.pathEndIdx == -1) {
            this.pathEndIdx = QueryStringDecoder.findPathEndIndex(this.uri);
        }
        return this.pathEndIdx;
    }

    /*
     * Enabled aggressive block sorting
     */
    private static Map<String, List<String>> decodeParams(String s, int from, Charset charset, int paramsLimit) {
        int i;
        int len = s.length();
        if (from >= len) {
            return Collections.emptyMap();
        }
        if (s.charAt(from) == '?') {
            ++from;
        }
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        int nameStart = from;
        int valueStart = -1;
        block5 : for (i = from; i < len; ++i) {
            switch (s.charAt(i)) {
                case '=': {
                    if (nameStart == i) {
                        nameStart = i + 1;
                        break;
                    }
                    if (valueStart >= nameStart) break;
                    valueStart = i + 1;
                    break;
                }
                case '&': 
                case ';': {
                    if (QueryStringDecoder.addParam(s, nameStart, valueStart, i, params, charset) && --paramsLimit == 0) {
                        return params;
                    }
                    nameStart = i + 1;
                    break;
                }
                case '#': {
                    break block5;
                }
            }
        }
        QueryStringDecoder.addParam(s, nameStart, valueStart, i, params, charset);
        return params;
    }

    private static boolean addParam(String s, int nameStart, int valueStart, int valueEnd, Map<String, List<String>> params, Charset charset) {
        if (nameStart >= valueEnd) {
            return false;
        }
        if (valueStart <= nameStart) {
            valueStart = valueEnd + 1;
        }
        String name = QueryStringDecoder.decodeComponent(s, nameStart, valueStart - 1, charset, false);
        String value = QueryStringDecoder.decodeComponent(s, valueStart, valueEnd, charset, false);
        List<String> values = params.get(name);
        if (values == null) {
            values = new ArrayList<String>(1);
            params.put(name, values);
        }
        values.add(value);
        return true;
    }

    public static String decodeComponent(String s) {
        return QueryStringDecoder.decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
    }

    public static String decodeComponent(String s, Charset charset) {
        if (s == null) {
            return "";
        }
        return QueryStringDecoder.decodeComponent(s, 0, s.length(), charset, false);
    }

    private static String decodeComponent(String s, int from, int toExcluded, Charset charset, boolean isPath) {
        int len = toExcluded - from;
        if (len <= 0) {
            return "";
        }
        int firstEscaped = -1;
        for (int i = from; i < toExcluded; ++i) {
            char c = s.charAt(i);
            if (c != '%' && (c != '+' || isPath)) continue;
            firstEscaped = i;
            break;
        }
        if (firstEscaped == -1) {
            return s.substring(from, toExcluded);
        }
        CharsetDecoder decoder = CharsetUtil.decoder(charset);
        int decodedCapacity = (toExcluded - firstEscaped) / 3;
        ByteBuffer byteBuf = ByteBuffer.allocate(decodedCapacity);
        CharBuffer charBuf = CharBuffer.allocate(decodedCapacity);
        StringBuilder strBuf = new StringBuilder(len);
        strBuf.append(s, from, firstEscaped);
        for (int i = firstEscaped; i < toExcluded; ++i) {
            char c = s.charAt(i);
            if (c != '%') {
                strBuf.append(c != '+' || isPath ? c : (char)' ');
                continue;
            }
            byteBuf.clear();
            do {
                if (i + 3 > toExcluded) {
                    throw new IllegalArgumentException("unterminated escape sequence at index " + i + " of: " + s);
                }
                byteBuf.put(StringUtil.decodeHexByte(s, i + 1));
            } while ((i += 3) < toExcluded && s.charAt(i) == '%');
            --i;
            byteBuf.flip();
            charBuf.clear();
            CoderResult result = decoder.reset().decode(byteBuf, charBuf, true);
            try {
                if (!result.isUnderflow()) {
                    result.throwException();
                }
                if (!(result = decoder.flush(charBuf)).isUnderflow()) {
                    result.throwException();
                }
            }
            catch (CharacterCodingException ex) {
                throw new IllegalStateException(ex);
            }
            strBuf.append(charBuf.flip());
        }
        return strBuf.toString();
    }

    private static int findPathEndIndex(String uri) {
        int len = uri.length();
        for (int i = 0; i < len; ++i) {
            char c = uri.charAt(i);
            if (c != '?' && c != '#') continue;
            return i;
        }
        return len;
    }
}

