/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Error;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Http2Exception
extends Exception {
    private static final long serialVersionUID = -6941186345430164209L;
    private final Http2Error error;
    private final ShutdownHint shutdownHint;

    public Http2Exception(Http2Error error) {
        this(error, ShutdownHint.HARD_SHUTDOWN);
    }

    public Http2Exception(Http2Error error, ShutdownHint shutdownHint) {
        this.error = ObjectUtil.checkNotNull(error, "error");
        this.shutdownHint = ObjectUtil.checkNotNull(shutdownHint, "shutdownHint");
    }

    public Http2Exception(Http2Error error, String message) {
        this(error, message, ShutdownHint.HARD_SHUTDOWN);
    }

    public Http2Exception(Http2Error error, String message, ShutdownHint shutdownHint) {
        super(message);
        this.error = ObjectUtil.checkNotNull(error, "error");
        this.shutdownHint = ObjectUtil.checkNotNull(shutdownHint, "shutdownHint");
    }

    public Http2Exception(Http2Error error, String message, Throwable cause) {
        this(error, message, cause, ShutdownHint.HARD_SHUTDOWN);
    }

    public Http2Exception(Http2Error error, String message, Throwable cause, ShutdownHint shutdownHint) {
        super(message, cause);
        this.error = ObjectUtil.checkNotNull(error, "error");
        this.shutdownHint = ObjectUtil.checkNotNull(shutdownHint, "shutdownHint");
    }

    public Http2Error error() {
        return this.error;
    }

    public ShutdownHint shutdownHint() {
        return this.shutdownHint;
    }

    public static /* varargs */ Http2Exception connectionError(Http2Error error, String fmt, Object ... args) {
        return new Http2Exception(error, String.format(fmt, args));
    }

    public static /* varargs */ Http2Exception connectionError(Http2Error error, Throwable cause, String fmt, Object ... args) {
        return new Http2Exception(error, String.format(fmt, args), cause);
    }

    public static /* varargs */ Http2Exception closedStreamError(Http2Error error, String fmt, Object ... args) {
        return new ClosedStreamCreationException(error, String.format(fmt, args));
    }

    public static /* varargs */ Http2Exception streamError(int id, Http2Error error, String fmt, Object ... args) {
        return 0 == id ? Http2Exception.connectionError(error, fmt, args) : new StreamException(id, error, String.format(fmt, args));
    }

    public static /* varargs */ Http2Exception streamError(int id, Http2Error error, Throwable cause, String fmt, Object ... args) {
        return 0 == id ? Http2Exception.connectionError(error, cause, fmt, args) : new StreamException(id, error, String.format(fmt, args), cause);
    }

    public static /* varargs */ Http2Exception headerListSizeError(int id, Http2Error error, boolean onDecode, String fmt, Object ... args) {
        return 0 == id ? Http2Exception.connectionError(error, fmt, args) : new HeaderListSizeException(id, error, String.format(fmt, args), onDecode);
    }

    public static boolean isStreamError(Http2Exception e) {
        return e instanceof StreamException;
    }

    public static int streamId(Http2Exception e) {
        return Http2Exception.isStreamError(e) ? ((StreamException)e).streamId() : 0;
    }

    public static final class CompositeStreamException
    extends Http2Exception
    implements Iterable<StreamException> {
        private static final long serialVersionUID = 7091134858213711015L;
        private final List<StreamException> exceptions;

        public CompositeStreamException(Http2Error error, int initialCapacity) {
            super(error, ShutdownHint.NO_SHUTDOWN);
            this.exceptions = new ArrayList<StreamException>(initialCapacity);
        }

        public void add(StreamException e) {
            this.exceptions.add(e);
        }

        @Override
        public Iterator<StreamException> iterator() {
            return this.exceptions.iterator();
        }
    }

    public static final class HeaderListSizeException
    extends StreamException {
        private static final long serialVersionUID = -8807603212183882637L;
        private final boolean decode;

        HeaderListSizeException(int streamId, Http2Error error, String message, boolean decode) {
            super(streamId, error, message);
            this.decode = decode;
        }

        public boolean duringDecode() {
            return this.decode;
        }
    }

    public static class StreamException
    extends Http2Exception {
        private static final long serialVersionUID = 602472544416984384L;
        private final int streamId;

        StreamException(int streamId, Http2Error error, String message) {
            super(error, message, ShutdownHint.NO_SHUTDOWN);
            this.streamId = streamId;
        }

        StreamException(int streamId, Http2Error error, String message, Throwable cause) {
            super(error, message, cause, ShutdownHint.NO_SHUTDOWN);
            this.streamId = streamId;
        }

        public int streamId() {
            return this.streamId;
        }
    }

    public static final class ClosedStreamCreationException
    extends Http2Exception {
        private static final long serialVersionUID = -6746542974372246206L;

        public ClosedStreamCreationException(Http2Error error) {
            super(error);
        }

        public ClosedStreamCreationException(Http2Error error, String message) {
            super(error, message);
        }

        public ClosedStreamCreationException(Http2Error error, String message, Throwable cause) {
            super(error, message, cause);
        }
    }

    public static enum ShutdownHint {
        NO_SHUTDOWN,
        GRACEFUL_SHUTDOWN,
        HARD_SHUTDOWN;
        

        private ShutdownHint() {
        }
    }

}

