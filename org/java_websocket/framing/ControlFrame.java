/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.framing;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidFrameException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;

public abstract class ControlFrame
extends FramedataImpl1 {
    public ControlFrame(Framedata.Opcode opcode) {
        super(opcode);
    }

    public void isValid() throws InvalidDataException {
        if (!this.isFin()) {
            throw new InvalidFrameException("Control frame cant have fin==false set");
        }
        if (this.isRSV1()) {
            throw new InvalidFrameException("Control frame cant have rsv1==true set");
        }
        if (this.isRSV2()) {
            throw new InvalidFrameException("Control frame cant have rsv2==true set");
        }
        if (this.isRSV3()) {
            throw new InvalidFrameException("Control frame cant have rsv3==true set");
        }
    }
}

