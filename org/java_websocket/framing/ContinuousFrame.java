/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.framing;

import org.java_websocket.framing.DataFrame;
import org.java_websocket.framing.Framedata;

public class ContinuousFrame
extends DataFrame {
    public ContinuousFrame() {
        super(Framedata.Opcode.CONTINUOUS);
    }
}

