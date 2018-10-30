/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.exceptions;

import org.java_websocket.exceptions.InvalidDataException;

public class InvalidFrameException
extends InvalidDataException {
    private static final long serialVersionUID = -9016496369828887591L;

    public InvalidFrameException() {
        super(1002);
    }

    public InvalidFrameException(String s) {
        super(1002, s);
    }

    public InvalidFrameException(Throwable t) {
        super(1002, t);
    }

    public InvalidFrameException(String s, Throwable t) {
        super(1002, s, t);
    }
}

