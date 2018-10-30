/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.framing;

import org.java_websocket.framing.ControlFrame;
import org.java_websocket.framing.Framedata;

public class PingFrame
extends ControlFrame {
    public PingFrame() {
        super(Framedata.Opcode.PING);
    }
}

