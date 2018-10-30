/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v4;

public class Socks4CommandStatus
implements Comparable<Socks4CommandStatus> {
    public static final Socks4CommandStatus SUCCESS = new Socks4CommandStatus(90, "SUCCESS");
    public static final Socks4CommandStatus REJECTED_OR_FAILED = new Socks4CommandStatus(91, "REJECTED_OR_FAILED");
    public static final Socks4CommandStatus IDENTD_UNREACHABLE = new Socks4CommandStatus(92, "IDENTD_UNREACHABLE");
    public static final Socks4CommandStatus IDENTD_AUTH_FAILURE = new Socks4CommandStatus(93, "IDENTD_AUTH_FAILURE");
    private final byte byteValue;
    private final String name;
    private String text;

    public static Socks4CommandStatus valueOf(byte b) {
        switch (b) {
            case 90: {
                return SUCCESS;
            }
            case 91: {
                return REJECTED_OR_FAILED;
            }
            case 92: {
                return IDENTD_UNREACHABLE;
            }
            case 93: {
                return IDENTD_AUTH_FAILURE;
            }
        }
        return new Socks4CommandStatus(b);
    }

    public Socks4CommandStatus(int byteValue) {
        this(byteValue, "UNKNOWN");
    }

    public Socks4CommandStatus(int byteValue, String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.byteValue = (byte)byteValue;
        this.name = name;
    }

    public byte byteValue() {
        return this.byteValue;
    }

    public boolean isSuccess() {
        return this.byteValue == 90;
    }

    public int hashCode() {
        return this.byteValue;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Socks4CommandStatus)) {
            return false;
        }
        return this.byteValue == ((Socks4CommandStatus)obj).byteValue;
    }

    @Override
    public int compareTo(Socks4CommandStatus o) {
        return this.byteValue - o.byteValue;
    }

    public String toString() {
        String text = this.text;
        if (text == null) {
            this.text = text = this.name + '(' + (this.byteValue & 255) + ')';
        }
        return text;
    }
}

