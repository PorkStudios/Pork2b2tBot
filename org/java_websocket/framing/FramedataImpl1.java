/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.framing;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.BinaryFrame;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.ContinuousFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.framing.PongFrame;
import org.java_websocket.framing.TextFrame;
import org.java_websocket.util.ByteBufferUtils;

public abstract class FramedataImpl1
implements Framedata {
    private boolean fin;
    private Framedata.Opcode optcode;
    private ByteBuffer unmaskedpayload;
    private boolean transferemasked;
    private boolean rsv1;
    private boolean rsv2;
    private boolean rsv3;

    public abstract void isValid() throws InvalidDataException;

    public FramedataImpl1(Framedata.Opcode op) {
        this.optcode = op;
        this.unmaskedpayload = ByteBufferUtils.getEmptyByteBuffer();
        this.fin = true;
        this.transferemasked = false;
        this.rsv1 = false;
        this.rsv2 = false;
        this.rsv3 = false;
    }

    public boolean isRSV1() {
        return this.rsv1;
    }

    public boolean isRSV2() {
        return this.rsv2;
    }

    public boolean isRSV3() {
        return this.rsv3;
    }

    public boolean isFin() {
        return this.fin;
    }

    public Framedata.Opcode getOpcode() {
        return this.optcode;
    }

    public boolean getTransfereMasked() {
        return this.transferemasked;
    }

    public ByteBuffer getPayloadData() {
        return this.unmaskedpayload;
    }

    public void append(Framedata nextframe) {
        ByteBuffer b = nextframe.getPayloadData();
        if (this.unmaskedpayload == null) {
            this.unmaskedpayload = ByteBuffer.allocate(b.remaining());
            b.mark();
            this.unmaskedpayload.put(b);
            b.reset();
        } else {
            b.mark();
            this.unmaskedpayload.position(this.unmaskedpayload.limit());
            this.unmaskedpayload.limit(this.unmaskedpayload.capacity());
            if (b.remaining() > this.unmaskedpayload.remaining()) {
                ByteBuffer tmp = ByteBuffer.allocate(b.remaining() + this.unmaskedpayload.capacity());
                this.unmaskedpayload.flip();
                tmp.put(this.unmaskedpayload);
                tmp.put(b);
                this.unmaskedpayload = tmp;
            } else {
                this.unmaskedpayload.put(b);
            }
            this.unmaskedpayload.rewind();
            b.reset();
        }
        this.fin = nextframe.isFin();
    }

    public String toString() {
        return "Framedata{ optcode:" + (Object)((Object)this.getOpcode()) + ", fin:" + this.isFin() + ", rsv1:" + this.isRSV1() + ", rsv2:" + this.isRSV2() + ", rsv3:" + this.isRSV3() + ", payloadlength:[pos:" + this.unmaskedpayload.position() + ", len:" + this.unmaskedpayload.remaining() + "], payload:" + (this.unmaskedpayload.remaining() > 1000 ? "(too big to display)" : new String(this.unmaskedpayload.array())) + '}';
    }

    public void setPayload(ByteBuffer payload) {
        this.unmaskedpayload = payload;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public void setRSV1(boolean rsv1) {
        this.rsv1 = rsv1;
    }

    public void setRSV2(boolean rsv2) {
        this.rsv2 = rsv2;
    }

    public void setRSV3(boolean rsv3) {
        this.rsv3 = rsv3;
    }

    public void setTransferemasked(boolean transferemasked) {
        this.transferemasked = transferemasked;
    }

    public static FramedataImpl1 get(Framedata.Opcode opcode) {
        if (opcode == null) {
            throw new IllegalArgumentException("Supplied opcode cannot be null");
        }
        switch (opcode) {
            case PING: {
                return new PingFrame();
            }
            case PONG: {
                return new PongFrame();
            }
            case TEXT: {
                return new TextFrame();
            }
            case BINARY: {
                return new BinaryFrame();
            }
            case CLOSING: {
                return new CloseFrame();
            }
            case CONTINUOUS: {
                return new ContinuousFrame();
            }
        }
        throw new IllegalArgumentException("Supplied opcode is invalid");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FramedataImpl1 that = (FramedataImpl1)o;
        if (this.fin != that.fin) {
            return false;
        }
        if (this.transferemasked != that.transferemasked) {
            return false;
        }
        if (this.rsv1 != that.rsv1) {
            return false;
        }
        if (this.rsv2 != that.rsv2) {
            return false;
        }
        if (this.rsv3 != that.rsv3) {
            return false;
        }
        if (this.optcode != that.optcode) {
            return false;
        }
        return this.unmaskedpayload != null ? this.unmaskedpayload.equals(that.unmaskedpayload) : that.unmaskedpayload == null;
    }

    public int hashCode() {
        int result = this.fin ? 1 : 0;
        result = 31 * result + this.optcode.hashCode();
        result = 31 * result + (this.unmaskedpayload != null ? this.unmaskedpayload.hashCode() : 0);
        result = 31 * result + (this.transferemasked ? 1 : 0);
        result = 31 * result + (this.rsv1 ? 1 : 0);
        result = 31 * result + (this.rsv2 ? 1 : 0);
        result = 31 * result + (this.rsv3 ? 1 : 0);
        return result;
    }

}

