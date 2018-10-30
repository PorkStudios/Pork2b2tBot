/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FlowController;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2RemoteFlowController;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.Http2StreamVisitor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public interface Http2Connection {
    public Future<Void> close(Promise<Void> var1);

    public PropertyKey newKey();

    public void addListener(Listener var1);

    public void removeListener(Listener var1);

    public Http2Stream stream(int var1);

    public boolean streamMayHaveExisted(int var1);

    public Http2Stream connectionStream();

    public int numActiveStreams();

    public Http2Stream forEachActiveStream(Http2StreamVisitor var1) throws Http2Exception;

    public boolean isServer();

    public Endpoint<Http2LocalFlowController> local();

    public Endpoint<Http2RemoteFlowController> remote();

    public boolean goAwayReceived();

    public void goAwayReceived(int var1, long var2, ByteBuf var4);

    public boolean goAwaySent();

    public void goAwaySent(int var1, long var2, ByteBuf var4);

    public static interface PropertyKey {
    }

    public static interface Endpoint<F extends Http2FlowController> {
        public int incrementAndGetNextStreamId();

        public boolean isValidStreamId(int var1);

        public boolean mayHaveCreatedStream(int var1);

        public boolean created(Http2Stream var1);

        public boolean canOpenStream();

        public Http2Stream createStream(int var1, boolean var2) throws Http2Exception;

        public Http2Stream reservePushStream(int var1, Http2Stream var2) throws Http2Exception;

        public boolean isServer();

        public void allowPushTo(boolean var1);

        public boolean allowPushTo();

        public int numActiveStreams();

        public int maxActiveStreams();

        public void maxActiveStreams(int var1);

        public int lastStreamCreated();

        public int lastStreamKnownByPeer();

        public F flowController();

        public void flowController(F var1);

        public Endpoint<? extends Http2FlowController> opposite();
    }

    public static interface Listener {
        public void onStreamAdded(Http2Stream var1);

        public void onStreamActive(Http2Stream var1);

        public void onStreamHalfClosed(Http2Stream var1);

        public void onStreamClosed(Http2Stream var1);

        public void onStreamRemoved(Http2Stream var1);

        public void onGoAwaySent(int var1, long var2, ByteBuf var4);

        public void onGoAwayReceived(int var1, long var2, ByteBuf var4);
    }

}

