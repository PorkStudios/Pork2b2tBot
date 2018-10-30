/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v4;

public class Socks4CommandType
implements Comparable<Socks4CommandType> {
    public static final Socks4CommandType CONNECT = new Socks4CommandType(1, "CONNECT");
    public static final Socks4CommandType BIND = new Socks4CommandType(2, "BIND");
    private final byte byteValue;
    private final String name;
    private String text;

    public static Socks4CommandType valueOf(byte b) {
        switch (b) {
            case 1: {
                return CONNECT;
            }
            case 2: {
                return BIND;
            }
        }
        return new Socks4CommandType(b);
    }

    public Socks4CommandType(int byteValue) {
        this(byteValue, "UNKNOWN");
    }

    public Socks4CommandType(int byteValue, String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.byteValue = (byte)byteValue;
        this.name = name;
    }

    public byte byteValue() {
        return this.byteValue;
    }

    public int hashCode() {
        return this.byteValue;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Socks4CommandType)) {
            return false;
        }
        return this.byteValue == ((Socks4CommandType)obj).byteValue;
    }

    @Override
    public int compareTo(Socks4CommandType o) {
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

