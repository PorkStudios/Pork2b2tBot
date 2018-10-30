/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.framing;

import java.nio.ByteBuffer;

public interface Framedata {
    public boolean isFin();

    public boolean isRSV1();

    public boolean isRSV2();

    public boolean isRSV3();

    public boolean getTransfereMasked();

    public Opcode getOpcode();

    public ByteBuffer getPayloadData();

    public void append(Framedata var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Opcode {
        CONTINUOUS,
        TEXT,
        BINARY,
        PING,
        PONG,
        CLOSING;
        

        private Opcode() {
        }
    }

}

