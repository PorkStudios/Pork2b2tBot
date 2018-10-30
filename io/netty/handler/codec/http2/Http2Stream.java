/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2Exception;

public interface Http2Stream {
    public int id();

    public State state();

    public Http2Stream open(boolean var1) throws Http2Exception;

    public Http2Stream close();

    public Http2Stream closeLocalSide();

    public Http2Stream closeRemoteSide();

    public boolean isResetSent();

    public Http2Stream resetSent();

    public <V> V setProperty(Http2Connection.PropertyKey var1, V var2);

    public <V> V getProperty(Http2Connection.PropertyKey var1);

    public <V> V removeProperty(Http2Connection.PropertyKey var1);

    public Http2Stream headersSent(boolean var1);

    public boolean isHeadersSent();

    public boolean isTrailersSent();

    public Http2Stream headersReceived(boolean var1);

    public boolean isHeadersReceived();

    public boolean isTrailersReceived();

    public Http2Stream pushPromiseSent();

    public boolean isPushPromiseSent();

    public static enum State {
        IDLE(false, false),
        RESERVED_LOCAL(false, false),
        RESERVED_REMOTE(false, false),
        OPEN(true, true),
        HALF_CLOSED_LOCAL(false, true),
        HALF_CLOSED_REMOTE(true, false),
        CLOSED(false, false);
        
        private final boolean localSideOpen;
        private final boolean remoteSideOpen;

        private State(boolean localSideOpen, boolean remoteSideOpen) {
            this.localSideOpen = localSideOpen;
            this.remoteSideOpen = remoteSideOpen;
        }

        public boolean localSideOpen() {
            return this.localSideOpen;
        }

        public boolean remoteSideOpen() {
            return this.remoteSideOpen;
        }
    }

}

