/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import org.java_websocket.AbstractWrappedByteChannel;

@Deprecated
public abstract class AbstractClientProxyChannel
extends AbstractWrappedByteChannel {
    protected final ByteBuffer proxyHandshake;

    @Deprecated
    public AbstractClientProxyChannel(ByteChannel towrap) {
        super(towrap);
        try {
            this.proxyHandshake = ByteBuffer.wrap(this.buildHandShake().getBytes("ASCII"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public int write(ByteBuffer src) throws IOException {
        if (!this.proxyHandshake.hasRemaining()) {
            return super.write(src);
        }
        return super.write(this.proxyHandshake);
    }

    @Deprecated
    public abstract String buildHandShake();
}

