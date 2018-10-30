/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.exceptions;

public class NotSendableException
extends RuntimeException {
    private static final long serialVersionUID = -6468967874576651628L;

    public NotSendableException(String s) {
        super(s);
    }

    public NotSendableException(Throwable t) {
        super(t);
    }

    public NotSendableException(String s, Throwable t) {
        super(s, t);
    }
}

