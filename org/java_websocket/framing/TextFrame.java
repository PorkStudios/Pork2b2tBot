/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.framing;

import java.nio.ByteBuffer;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.DataFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.util.Charsetfunctions;

public class TextFrame
extends DataFrame {
    public TextFrame() {
        super(Framedata.Opcode.TEXT);
    }

    public void isValid() throws InvalidDataException {
        super.isValid();
        if (!Charsetfunctions.isValidUTF8(this.getPayloadData())) {
            throw new InvalidDataException(1007, "Received text is no valid utf8 string!");
        }
    }
}

