/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

final class KQueueStaticallyReferencedJniMethods {
    private KQueueStaticallyReferencedJniMethods() {
    }

    static native short evAdd();

    static native short evEnable();

    static native short evDisable();

    static native short evDelete();

    static native short evClear();

    static native short evEOF();

    static native short evError();

    static native short noteReadClosed();

    static native short noteConnReset();

    static native short noteDisconnected();

    static native short evfiltRead();

    static native short evfiltWrite();

    static native short evfiltUser();

    static native short evfiltSock();
}

