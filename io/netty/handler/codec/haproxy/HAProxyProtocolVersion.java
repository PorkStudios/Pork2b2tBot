/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.haproxy;

public enum HAProxyProtocolVersion {
    V1(16),
    V2(32);
    
    private static final byte VERSION_MASK = -16;
    private final byte byteValue;

    private HAProxyProtocolVersion(byte byteValue) {
        this.byteValue = byteValue;
    }

    public static HAProxyProtocolVersion valueOf(byte verCmdByte) {
        int version = verCmdByte & -16;
        switch ((byte)version) {
            case 32: {
                return V2;
            }
            case 16: {
                return V1;
            }
        }
        throw new IllegalArgumentException("unknown version: " + version);
    }

    public byte byteValue() {
        return this.byteValue;
    }
}

