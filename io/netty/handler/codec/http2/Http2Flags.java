/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

public final class Http2Flags {
    public static final short END_STREAM = 1;
    public static final short END_HEADERS = 4;
    public static final short ACK = 1;
    public static final short PADDED = 8;
    public static final short PRIORITY = 32;
    private short value;

    public Http2Flags() {
    }

    public Http2Flags(short value) {
        this.value = value;
    }

    public short value() {
        return this.value;
    }

    public boolean endOfStream() {
        return this.isFlagSet((short)1);
    }

    public boolean endOfHeaders() {
        return this.isFlagSet((short)4);
    }

    public boolean priorityPresent() {
        return this.isFlagSet((short)32);
    }

    public boolean ack() {
        return this.isFlagSet((short)1);
    }

    public boolean paddingPresent() {
        return this.isFlagSet((short)8);
    }

    public int getNumPriorityBytes() {
        return this.priorityPresent() ? 5 : 0;
    }

    public int getPaddingPresenceFieldLength() {
        return this.paddingPresent() ? 1 : 0;
    }

    public Http2Flags endOfStream(boolean endOfStream) {
        return this.setFlag(endOfStream, (short)1);
    }

    public Http2Flags endOfHeaders(boolean endOfHeaders) {
        return this.setFlag(endOfHeaders, (short)4);
    }

    public Http2Flags priorityPresent(boolean priorityPresent) {
        return this.setFlag(priorityPresent, (short)32);
    }

    public Http2Flags paddingPresent(boolean paddingPresent) {
        return this.setFlag(paddingPresent, (short)8);
    }

    public Http2Flags ack(boolean ack) {
        return this.setFlag(ack, (short)1);
    }

    public Http2Flags setFlag(boolean on, short mask) {
        this.value = on ? (short)(this.value | mask) : (short)(this.value & ~ mask);
        return this;
    }

    public boolean isFlagSet(short mask) {
        return (this.value & mask) != 0;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.value;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return this.value == ((Http2Flags)obj).value;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("value = ").append(this.value).append(" (");
        if (this.ack()) {
            builder.append("ACK,");
        }
        if (this.endOfHeaders()) {
            builder.append("END_OF_HEADERS,");
        }
        if (this.endOfStream()) {
            builder.append("END_OF_STREAM,");
        }
        if (this.priorityPresent()) {
            builder.append("PRIORITY_PRESENT,");
        }
        if (this.paddingPresent()) {
            builder.append("PADDING_PRESENT,");
        }
        builder.append(')');
        return builder.toString();
    }
}

