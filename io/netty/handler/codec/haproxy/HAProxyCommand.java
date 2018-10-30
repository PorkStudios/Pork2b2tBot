/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.haproxy;

public enum HAProxyCommand {
    LOCAL(0),
    PROXY(1);
    
    private static final byte COMMAND_MASK = 15;
    private final byte byteValue;

    private HAProxyCommand(byte byteValue) {
        this.byteValue = byteValue;
    }

    public static HAProxyCommand valueOf(byte verCmdByte) {
        int cmd = verCmdByte & 15;
        switch ((byte)cmd) {
            case 1: {
                return PROXY;
            }
            case 0: {
                return LOCAL;
            }
        }
        throw new IllegalArgumentException("unknown command: " + cmd);
    }

    public byte byteValue() {
        return this.byteValue;
    }
}

