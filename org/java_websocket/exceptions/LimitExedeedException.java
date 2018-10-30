/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.exceptions;

import org.java_websocket.exceptions.InvalidDataException;

public class LimitExedeedException
extends InvalidDataException {
    private static final long serialVersionUID = 6908339749836826785L;

    public LimitExedeedException() {
        super(1009);
    }

    public LimitExedeedException(String s) {
        super(1009, s);
    }
}

