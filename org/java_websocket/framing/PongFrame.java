/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.framing;

import java.nio.ByteBuffer;
import org.java_websocket.framing.ControlFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;

public class PongFrame
extends ControlFrame {
    public PongFrame() {
        super(Framedata.Opcode.PONG);
    }

    public PongFrame(PingFrame pingFrame) {
        super(Framedata.Opcode.PONG);
        this.setPayload(pingFrame.getPayloadData());
    }
}

