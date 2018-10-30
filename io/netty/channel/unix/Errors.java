/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.unix;

import io.netty.channel.unix.ErrorsStaticallyReferencedJniMethods;
import io.netty.util.internal.EmptyArrays;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;

public final class Errors {
    public static final int ERRNO_ENOENT_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errnoENOENT();
    public static final int ERRNO_ENOTCONN_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errnoENOTCONN();
    public static final int ERRNO_EBADF_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errnoEBADF();
    public static final int ERRNO_EPIPE_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errnoEPIPE();
    public static final int ERRNO_ECONNRESET_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errnoECONNRESET();
    public static final int ERRNO_EAGAIN_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errnoEAGAIN();
    public static final int ERRNO_EWOULDBLOCK_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errnoEWOULDBLOCK();
    public static final int ERRNO_EINPROGRESS_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errnoEINPROGRESS();
    public static final int ERROR_ECONNREFUSED_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errorECONNREFUSED();
    public static final int ERROR_EISCONN_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errorEISCONN();
    public static final int ERROR_EALREADY_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errorEALREADY();
    public static final int ERROR_ENETUNREACH_NEGATIVE = - ErrorsStaticallyReferencedJniMethods.errorENETUNREACH();
    private static final String[] ERRORS = new String[512];

    static void throwConnectException(String method, NativeConnectException refusedCause, int err) throws IOException {
        if (err == refusedCause.expectedErr()) {
            throw refusedCause;
        }
        if (err == ERROR_EALREADY_NEGATIVE) {
            throw new ConnectionPendingException();
        }
        if (err == ERROR_ENETUNREACH_NEGATIVE) {
            throw new NoRouteToHostException();
        }
        if (err == ERROR_EISCONN_NEGATIVE) {
            throw new AlreadyConnectedException();
        }
        if (err == ERRNO_ENOENT_NEGATIVE) {
            throw new FileNotFoundException();
        }
        throw new ConnectException(method + "(..) failed: " + ERRORS[- err]);
    }

    public static NativeIoException newConnectionResetException(String method, int errnoNegative) {
        NativeIoException exception = Errors.newIOException(method, errnoNegative);
        exception.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        return exception;
    }

    public static NativeIoException newIOException(String method, int err) {
        return new NativeIoException(method, err);
    }

    public static int ioResult(String method, int err, NativeIoException resetCause, ClosedChannelException closedCause) throws IOException {
        if (err == ERRNO_EAGAIN_NEGATIVE || err == ERRNO_EWOULDBLOCK_NEGATIVE) {
            return 0;
        }
        if (err == resetCause.expectedErr()) {
            throw resetCause;
        }
        if (err == ERRNO_EBADF_NEGATIVE) {
            throw closedCause;
        }
        if (err == ERRNO_ENOTCONN_NEGATIVE) {
            throw new NotYetConnectedException();
        }
        if (err == ERRNO_ENOENT_NEGATIVE) {
            throw new FileNotFoundException();
        }
        throw Errors.newIOException(method, err);
    }

    private Errors() {
    }

    static {
        for (int i = 0; i < ERRORS.length; ++i) {
            Errors.ERRORS[i] = ErrorsStaticallyReferencedJniMethods.strError(i);
        }
    }

    static final class NativeConnectException
    extends ConnectException {
        private static final long serialVersionUID = -5532328671712318161L;
        private final int expectedErr;

        NativeConnectException(String method, int expectedErr) {
            super(method + "(..) failed: " + ERRORS[- expectedErr]);
            this.expectedErr = expectedErr;
        }

        int expectedErr() {
            return this.expectedErr;
        }
    }

    public static final class NativeIoException
    extends IOException {
        private static final long serialVersionUID = 8222160204268655526L;
        private final int expectedErr;

        public NativeIoException(String method, int expectedErr) {
            super(method + "(..) failed: " + ERRORS[- expectedErr]);
            this.expectedErr = expectedErr;
        }

        public int expectedErr() {
            return this.expectedErr;
        }
    }

}

