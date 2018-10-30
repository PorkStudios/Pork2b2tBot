/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class HttpUtil {
    private static final AsciiString CHARSET_EQUALS = AsciiString.of(HttpHeaderValues.CHARSET + "=");
    private static final AsciiString SEMICOLON = AsciiString.cached(";");

    private HttpUtil() {
    }

    public static boolean isOriginForm(URI uri) {
        return uri.getScheme() == null && uri.getSchemeSpecificPart() == null && uri.getHost() == null && uri.getAuthority() == null;
    }

    public static boolean isAsteriskForm(URI uri) {
        return "*".equals(uri.getPath()) && uri.getScheme() == null && uri.getSchemeSpecificPart() == null && uri.getHost() == null && uri.getAuthority() == null && uri.getQuery() == null && uri.getFragment() == null;
    }

    public static boolean isKeepAlive(HttpMessage message) {
        String connection = message.headers().get(HttpHeaderNames.CONNECTION);
        if (connection != null && HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(connection)) {
            return false;
        }
        if (message.protocolVersion().isKeepAliveDefault()) {
            return !HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(connection);
        }
        return HttpHeaderValues.KEEP_ALIVE.contentEqualsIgnoreCase(connection);
    }

    public static void setKeepAlive(HttpMessage message, boolean keepAlive) {
        HttpUtil.setKeepAlive(message.headers(), message.protocolVersion(), keepAlive);
    }

    public static void setKeepAlive(HttpHeaders h, HttpVersion httpVersion, boolean keepAlive) {
        if (httpVersion.isKeepAliveDefault()) {
            if (keepAlive) {
                h.remove(HttpHeaderNames.CONNECTION);
            } else {
                h.set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.CLOSE);
            }
        } else if (keepAlive) {
            h.set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.KEEP_ALIVE);
        } else {
            h.remove(HttpHeaderNames.CONNECTION);
        }
    }

    public static long getContentLength(HttpMessage message) {
        String value = message.headers().get(HttpHeaderNames.CONTENT_LENGTH);
        if (value != null) {
            return Long.parseLong(value);
        }
        long webSocketContentLength = HttpUtil.getWebSocketContentLength(message);
        if (webSocketContentLength >= 0L) {
            return webSocketContentLength;
        }
        throw new NumberFormatException("header not found: " + HttpHeaderNames.CONTENT_LENGTH);
    }

    public static long getContentLength(HttpMessage message, long defaultValue) {
        String value = message.headers().get(HttpHeaderNames.CONTENT_LENGTH);
        if (value != null) {
            return Long.parseLong(value);
        }
        long webSocketContentLength = HttpUtil.getWebSocketContentLength(message);
        if (webSocketContentLength >= 0L) {
            return webSocketContentLength;
        }
        return defaultValue;
    }

    public static int getContentLength(HttpMessage message, int defaultValue) {
        return (int)Math.min(Integer.MAX_VALUE, HttpUtil.getContentLength(message, (long)defaultValue));
    }

    private static int getWebSocketContentLength(HttpMessage message) {
        HttpResponse res;
        HttpHeaders h = message.headers();
        if (message instanceof HttpRequest) {
            HttpRequest req = (HttpRequest)message;
            if (HttpMethod.GET.equals(req.method()) && h.contains(HttpHeaderNames.SEC_WEBSOCKET_KEY1) && h.contains(HttpHeaderNames.SEC_WEBSOCKET_KEY2)) {
                return 8;
            }
        } else if (message instanceof HttpResponse && (res = (HttpResponse)message).status().code() == 101 && h.contains(HttpHeaderNames.SEC_WEBSOCKET_ORIGIN) && h.contains(HttpHeaderNames.SEC_WEBSOCKET_LOCATION)) {
            return 16;
        }
        return -1;
    }

    public static void setContentLength(HttpMessage message, long length) {
        message.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)length);
    }

    public static boolean isContentLengthSet(HttpMessage m) {
        return m.headers().contains(HttpHeaderNames.CONTENT_LENGTH);
    }

    public static boolean is100ContinueExpected(HttpMessage message) {
        if (!HttpUtil.isExpectHeaderValid(message)) {
            return false;
        }
        String expectValue = message.headers().get(HttpHeaderNames.EXPECT);
        return HttpHeaderValues.CONTINUE.toString().equalsIgnoreCase(expectValue);
    }

    static boolean isUnsupportedExpectation(HttpMessage message) {
        if (!HttpUtil.isExpectHeaderValid(message)) {
            return false;
        }
        String expectValue = message.headers().get(HttpHeaderNames.EXPECT);
        return expectValue != null && !HttpHeaderValues.CONTINUE.toString().equalsIgnoreCase(expectValue);
    }

    private static boolean isExpectHeaderValid(HttpMessage message) {
        return message instanceof HttpRequest && message.protocolVersion().compareTo(HttpVersion.HTTP_1_1) >= 0;
    }

    public static void set100ContinueExpected(HttpMessage message, boolean expected) {
        if (expected) {
            message.headers().set((CharSequence)HttpHeaderNames.EXPECT, (Object)HttpHeaderValues.CONTINUE);
        } else {
            message.headers().remove(HttpHeaderNames.EXPECT);
        }
    }

    public static boolean isTransferEncodingChunked(HttpMessage message) {
        return message.headers().contains(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED, true);
    }

    public static void setTransferEncodingChunked(HttpMessage m, boolean chunked) {
        if (chunked) {
            m.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
            m.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
        } else {
            List<String> encodings = m.headers().getAll(HttpHeaderNames.TRANSFER_ENCODING);
            if (encodings.isEmpty()) {
                return;
            }
            ArrayList<String> values = new ArrayList<String>(encodings);
            Iterator<String> valuesIt = values.iterator();
            while (valuesIt.hasNext()) {
                CharSequence value = valuesIt.next();
                if (!HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase(value)) continue;
                valuesIt.remove();
            }
            if (values.isEmpty()) {
                m.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
            } else {
                m.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, values);
            }
        }
    }

    public static Charset getCharset(HttpMessage message) {
        return HttpUtil.getCharset(message, CharsetUtil.ISO_8859_1);
    }

    public static Charset getCharset(CharSequence contentTypeValue) {
        if (contentTypeValue != null) {
            return HttpUtil.getCharset(contentTypeValue, CharsetUtil.ISO_8859_1);
        }
        return CharsetUtil.ISO_8859_1;
    }

    public static Charset getCharset(HttpMessage message, Charset defaultCharset) {
        String contentTypeValue = message.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentTypeValue != null) {
            return HttpUtil.getCharset(contentTypeValue, defaultCharset);
        }
        return defaultCharset;
    }

    public static Charset getCharset(CharSequence contentTypeValue, Charset defaultCharset) {
        if (contentTypeValue != null) {
            CharSequence charsetCharSequence = HttpUtil.getCharsetAsSequence(contentTypeValue);
            if (charsetCharSequence != null) {
                try {
                    return Charset.forName(charsetCharSequence.toString());
                }
                catch (UnsupportedCharsetException ignored) {
                    return defaultCharset;
                }
            }
            return defaultCharset;
        }
        return defaultCharset;
    }

    @Deprecated
    public static CharSequence getCharsetAsString(HttpMessage message) {
        return HttpUtil.getCharsetAsSequence(message);
    }

    public static CharSequence getCharsetAsSequence(HttpMessage message) {
        String contentTypeValue = message.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentTypeValue != null) {
            return HttpUtil.getCharsetAsSequence(contentTypeValue);
        }
        return null;
    }

    public static CharSequence getCharsetAsSequence(CharSequence contentTypeValue) {
        int indexOfEncoding;
        if (contentTypeValue == null) {
            throw new NullPointerException("contentTypeValue");
        }
        int indexOfCharset = AsciiString.indexOfIgnoreCaseAscii(contentTypeValue, CHARSET_EQUALS, 0);
        if (indexOfCharset != -1 && (indexOfEncoding = indexOfCharset + CHARSET_EQUALS.length()) < contentTypeValue.length()) {
            return contentTypeValue.subSequence(indexOfEncoding, contentTypeValue.length());
        }
        return null;
    }

    public static CharSequence getMimeType(HttpMessage message) {
        String contentTypeValue = message.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentTypeValue != null) {
            return HttpUtil.getMimeType(contentTypeValue);
        }
        return null;
    }

    public static CharSequence getMimeType(CharSequence contentTypeValue) {
        if (contentTypeValue == null) {
            throw new NullPointerException("contentTypeValue");
        }
        int indexOfSemicolon = AsciiString.indexOfIgnoreCaseAscii(contentTypeValue, SEMICOLON, 0);
        if (indexOfSemicolon != -1) {
            return contentTypeValue.subSequence(0, indexOfSemicolon);
        }
        return contentTypeValue.length() > 0 ? contentTypeValue : null;
    }
}

