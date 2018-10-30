/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.server;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.java_websocket.SSLSocketChannel2;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.drafts.Draft;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultSSLWebSocketServerFactory
implements WebSocketServerFactory {
    protected SSLContext sslcontext;
    protected ExecutorService exec;

    public DefaultSSLWebSocketServerFactory(SSLContext sslContext) {
        this(sslContext, Executors.newSingleThreadScheduledExecutor());
    }

    public DefaultSSLWebSocketServerFactory(SSLContext sslContext, ExecutorService exec) {
        if (sslContext == null || exec == null) {
            throw new IllegalArgumentException();
        }
        this.sslcontext = sslContext;
        this.exec = exec;
    }

    @Override
    public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
        SSLEngine e = this.sslcontext.createSSLEngine();
        ArrayList<String> ciphers = new ArrayList<String>(Arrays.asList(e.getEnabledCipherSuites()));
        ciphers.remove("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        e.setEnabledCipherSuites(ciphers.toArray(new String[ciphers.size()]));
        e.setUseClientMode(false);
        return new SSLSocketChannel2(channel, e, this.exec, key);
    }

    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d) {
        return new WebSocketImpl((WebSocketListener)a, d);
    }

    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter a, List<Draft> d) {
        return new WebSocketImpl((WebSocketListener)a, d);
    }

    @Override
    public void close() {
        this.exec.shutdown();
    }
}

