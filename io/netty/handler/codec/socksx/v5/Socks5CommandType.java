/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v5;

public class Socks5CommandType
implements Comparable<Socks5CommandType> {
    public static final Socks5CommandType CONNECT = new Socks5CommandType(1, "CONNECT");
    public static final Socks5CommandType BIND = new Socks5CommandType(2, "BIND");
    public static final Socks5CommandType UDP_ASSOCIATE = new Socks5CommandType(3, "UDP_ASSOCIATE");
    private final byte byteValue;
    private final String name;
    private String text;

    public static Socks5CommandType valueOf(byte b) {
        switch (b) {
            case 1: {
                return CONNECT;
            }
            case 2: {
                return BIND;
            }
            case 3: {
                return UDP_ASSOCIATE;
            }
        }
        return new Socks5CommandType(b);
    }

    public Socks5CommandType(int byteValue) {
        this(byteValue, "UNKNOWN");
    }

    public Socks5CommandType(int byteValue, String name) {
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
        if (!(obj instanceof Socks5CommandType)) {
            return false;
        }
        return this.byteValue == ((Socks5CommandType)obj).byteValue;
    }

    @Override
    public int compareTo(Socks5CommandType o) {
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

