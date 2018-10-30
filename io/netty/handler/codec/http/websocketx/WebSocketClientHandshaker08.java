/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket08FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.URI;
import java.nio.charset.Charset;

public class WebSocketClientHandshaker08
extends WebSocketClientHandshaker {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker08.class);
    public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private String expectedChallengeResponseString;
    private final boolean allowExtensions;
    private final boolean performMasking;
    private final boolean allowMaskMismatch;

    public WebSocketClientHandshaker08(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, false);
    }

    public WebSocketClientHandshaker08(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch) {
        super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength);
        this.allowExtensions = allowExtensions;
        this.performMasking = performMasking;
        this.allowMaskMismatch = allowMaskMismatch;
    }

    @Override
    protected FullHttpRequest newHandshakeRequest() {
        URI wsURL = this.uri();
        String path = WebSocketClientHandshaker08.rawPath(wsURL);
        byte[] nonce = WebSocketUtil.randomBytes(16);
        String key = WebSocketUtil.base64(nonce);
        String acceptSeed = key + MAGIC_GUID;
        byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
        this.expectedChallengeResponseString = WebSocketUtil.base64(sha1);
        if (logger.isDebugEnabled()) {
            logger.debug("WebSocket version 08 client handshake key: {}, expected response: {}", (Object)key, (Object)this.expectedChallengeResponseString);
        }
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
        HttpHeaders headers = request.headers();
        headers.add((CharSequence)HttpHeaderNames.UPGRADE, (Object)HttpHeaderValues.WEBSOCKET).add((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE).add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY, (Object)key).add((CharSequence)HttpHeaderNames.HOST, (Object)WebSocketClientHandshaker08.websocketHostValue(wsURL)).add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, (Object)WebSocketClientHandshaker08.websocketOriginValue(wsURL));
        String expectedSubprotocol = this.expectedSubprotocol();
        if (expectedSubprotocol != null && !expectedSubprotocol.isEmpty()) {
            headers.add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)expectedSubprotocol);
        }
        headers.add((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, (Object)"8");
        if (this.customHeaders != null) {
            headers.add(this.customHeaders);
        }
        return request;
    }

    @Override
    protected void verify(FullHttpResponse response) {
        HttpResponseStatus status = HttpResponseStatus.SWITCHING_PROTOCOLS;
        HttpHeaders headers = response.headers();
        if (!response.status().equals(status)) {
            throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + response.status());
        }
        String upgrade = headers.get(HttpHeaderNames.UPGRADE);
        if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
            throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
        }
        if (!headers.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            throw new WebSocketHandshakeException("Invalid handshake response connection: " + headers.get(HttpHeaderNames.CONNECTION));
        }
        String accept = headers.get(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
        if (accept == null || !accept.equals(this.expectedChallengeResponseString)) {
            throw new WebSocketHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", accept, this.expectedChallengeResponseString));
        }
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket08FrameDecoder(false, this.allowExtensions, this.maxFramePayloadLength(), this.allowMaskMismatch);
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket08FrameEncoder(this.performMasking);
    }
}

